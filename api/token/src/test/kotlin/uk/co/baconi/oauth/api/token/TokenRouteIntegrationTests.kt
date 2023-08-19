package uk.co.baconi.oauth.api.token

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.shouldBe
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.ContentType.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.MethodNotAllowed
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.http.HttpStatusCode.Companion.UnsupportedMediaType
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import uk.co.baconi.oauth.api.common.AuthenticationModule
import uk.co.baconi.oauth.api.common.CommonModule.common
import uk.co.baconi.oauth.api.common.authentication.AuthenticatedUsername
import uk.co.baconi.oauth.api.common.authorisation.AuthorisationCode
import uk.co.baconi.oauth.api.common.authorisation.AuthorisationCodeRepository
import uk.co.baconi.oauth.api.common.authorisation.AuthorisationCodeTable
import uk.co.baconi.oauth.api.common.client.ClientConfigurationRepository
import uk.co.baconi.oauth.api.common.client.ClientId
import uk.co.baconi.oauth.api.common.client.ClientSecretRepository
import uk.co.baconi.oauth.api.common.client.ClientSecretService
import uk.co.baconi.oauth.api.common.scope.Scope.OpenId
import uk.co.baconi.oauth.api.common.token.*
import uk.co.baconi.oauth.api.common.token.TokenType.Bearer
import java.time.Instant
import java.time.temporal.ChronoUnit.DAYS
import java.util.*

class TokenRouteIntegrationTests : AuthenticationModule, TokenRoute {

    companion object {

        private const val TOKEN_ENDPOINT = "/token"

        private val database = Database.connect(
            url = "jdbc:h2:mem:TokenRouteIntegrationTests;DB_CLOSE_DELAY=60;",
            driver = "org.h2.Driver"
        )

        init {
            transaction(database) {
                SchemaUtils.create(AccessTokenTable)
                SchemaUtils.create(RefreshTokenTable)
                SchemaUtils.create(AuthorisationCodeTable)
            }
        }
    }


    private val accessTokenRepository = AccessTokenRepository(database)
    private val refreshTokenRepository = RefreshTokenRepository(database)

    override val accessTokenService = AccessTokenService(accessTokenRepository)
    override val refreshTokenService = RefreshTokenService(refreshTokenRepository)

    private val clientSecretRepository = ClientSecretRepository()
    private val clientConfigurationRepository = ClientConfigurationRepository()
    override val clientSecretService = ClientSecretService(clientSecretRepository, clientConfigurationRepository)

    override val authorisationCodeRepository = AuthorisationCodeRepository(database)

    override val passwordGrant = mockk<PasswordGrant>()
    override val assertionGrant = mockk<AssertionGrant>()
    override val refreshTokenGrant = mockk<RefreshTokenGrant>()
    override val authorisationCodeGrant = mockk<AuthorisationCodeGrant>()

    private fun setupApplication(block: suspend ApplicationTestBuilder.(HttpClient) -> Unit) {
        testApplication {
            application {
                common()
                authentication()
            }
            routing {
                token() // underTest
            }
            block(
                createClient {
                    install(ContentNegotiation) {
                        json()
                    }
                }
            )
        }
    }

    @Nested
    inner class InvalidHttpRequest {

        @ParameterizedTest
        @CsvSource("GET", "PUT", "PATCH", "DELETE", "HEAD", "OPTIONS")
        fun `should only support post requests`(method: String) = setupApplication { client ->

            val response = client.request(TOKEN_ENDPOINT) {
                this.method = HttpMethod.parse(method)
            }

            response.status shouldBe MethodNotAllowed
        }

        @Test
        fun `should require client authentication`() = setupApplication { client ->

            val response = client.post(TOKEN_ENDPOINT)

            response.status shouldBe Unauthorized
        }

        @ParameterizedTest
        @CsvSource("""json,{"grant_type":"aardvark"}""", """xml,<grantType>aardvark</grantType>""")
        fun `should only support url encoded form requests`(type: String, body: String) = setupApplication { client ->

            val response = client.post(TOKEN_ENDPOINT) {
                basicAuth("confidential-cicada", "9VylF3DbEeJbtdbih3lqpNXBw@Non#bi")
                contentType(ContentType("application", type))
                setBody(body)
            }

            response.status shouldBe UnsupportedMediaType
        }
    }

    @Nested
    inner class InvalidTokenRequest {

        @Test
        fun `should return BadRequest for invalid token requests`() = setupApplication { client ->

            val response = client.post(TOKEN_ENDPOINT) {
                basicAuth("confidential-cicada", "9VylF3DbEeJbtdbih3lqpNXBw@Non#bi")
                contentType(Application.FormUrlEncoded)
                setBody("grant_type=aardvark")
            }

            val body = response.body<Map<String, String>>()

            assertSoftly {
                response.status shouldBe BadRequest
                body shouldContain ("error" to "unsupported_grant_type")
                body shouldContain ("error_description" to "unsupported: aardvark")
            }
        }
    }

    @Nested
    inner class SuccessTokenRequest {

        @Test
        fun `should return OK for valid password grants`() = setupApplication { client ->

            val state = UUID.randomUUID()
            val accessToken = UUID.randomUUID()
            val refreshToken = UUID.randomUUID()

            every { passwordGrant.exchange(any()) } returns TokenResponse.Success(
                accessToken = accessToken,
                refreshToken = refreshToken,
                tokenType = Bearer,
                expiresIn = 5,
                scope = setOf(OpenId),
                state = "$state"
            )

            val response = client.post(TOKEN_ENDPOINT) {
                basicAuth("confidential-cicada", "9VylF3DbEeJbtdbih3lqpNXBw@Non#bi")
                contentType(Application.FormUrlEncoded)
                setBody("grant_type=password&username=aardvark&password=121212&scope=openid")
            }

            val body = response.body<Map<String, String>>()

            assertSoftly {
                response.status shouldBe OK
                body shouldContain ("access_token" to "$accessToken")
                body shouldContain ("refresh_token" to "$refreshToken")
                body shouldContain ("token_type" to "bearer")
                body shouldContain ("expires_in" to "5")
                body shouldContain ("scope" to "openid")
                body shouldContain ("state" to "$state")
            }
        }

        @Test
        fun `should return OK for valid authorisation code grants`() = setupApplication { client ->

            val state = UUID.randomUUID()
            val accessToken = UUID.randomUUID()
            val refreshToken = UUID.randomUUID()

            every { authorisationCodeGrant.exchange(any()) } returns TokenResponse.Success(
                accessToken = accessToken,
                refreshToken = refreshToken,
                expiresIn = 5,
                scope = setOf(OpenId),
                state = "$state"
            )

            val code = UUID.randomUUID()
            val now = Instant.now()
            authorisationCodeRepository.insert(
                AuthorisationCode.Basic(
                    value = code,
                    issuedAt = now,
                    expiresAt = now.plus(1, DAYS),
                    clientId = ClientId("confidential-cicada"),
                    username = AuthenticatedUsername("aardvark"),
                    redirectUri = "https://redirect.baconi.co.uk",
                    scopes = setOf(OpenId),
                    state = "$state"
                )
            )

            val response = client.post(TOKEN_ENDPOINT) {
                basicAuth("confidential-cicada", "9VylF3DbEeJbtdbih3lqpNXBw@Non#bi")
                contentType(Application.FormUrlEncoded)
                setBody("grant_type=authorization_code&code=$code&redirect_uri=https%3A%2F%2Fredirect.baconi.co.uk")
            }

            val body = response.body<Map<String, String>>()

            assertSoftly {
                response.status shouldBe OK
                body shouldContain ("access_token" to "$accessToken")
                body shouldContain ("refresh_token" to "$refreshToken")
                body shouldContain ("token_type" to "bearer")
                body shouldContain ("expires_in" to "5")
                body shouldContain ("scope" to "openid")
                body shouldContain ("state" to "$state")
            }
        }

        @Test
        fun `should return OK for valid refresh token grants`() = setupApplication { client ->

            val state = UUID.randomUUID()
            val accessToken = UUID.randomUUID()
            val refreshToken = UUID.randomUUID()

            every { refreshTokenGrant.exchange(any()) } returns TokenResponse.Success(
                accessToken = accessToken,
                refreshToken = refreshToken,
                expiresIn = 5,
                scope = setOf(OpenId),
                state = "$state"
            )

            val token = UUID.randomUUID()
            val now = Instant.now()
            refreshTokenRepository.insert(
                RefreshToken(
                    value = token,
                    issuedAt = now,
                    expiresAt = now.plus(1, DAYS),
                    notBefore = now,
                    clientId = ClientId("confidential-cicada"),
                    username = AuthenticatedUsername("aardvark"),
                    scopes = setOf(OpenId),
                )
            )

            val response = client.post(TOKEN_ENDPOINT) {
                basicAuth("confidential-cicada", "9VylF3DbEeJbtdbih3lqpNXBw@Non#bi")
                contentType(Application.FormUrlEncoded)
                setBody("grant_type=refresh_token&refresh_token=$token&scope=openid")
            }

            val body = response.body<Map<String, String>>()

            assertSoftly {
                response.status shouldBe OK
                body shouldContain ("access_token" to "$accessToken")
                body shouldContain ("refresh_token" to "$refreshToken")
                body shouldContain ("token_type" to "bearer")
                body shouldContain ("expires_in" to "5")
                body shouldContain ("scope" to "openid")
                body shouldContain ("state" to "$state")
            }
        }

        @Test
        fun `should return OK for valid assertion grants`() = setupApplication { client ->

            val state = UUID.randomUUID()
            val accessToken = UUID.randomUUID()
            val refreshToken = UUID.randomUUID()

            every { assertionGrant.exchange(any()) } returns TokenResponse.Success(
                accessToken = accessToken,
                refreshToken = refreshToken,
                expiresIn = 5,
                scope = setOf(OpenId),
                state = "$state"
            )

            val assertion = UUID.randomUUID()

            val response = client.post(TOKEN_ENDPOINT) {
                basicAuth("confidential-cicada", "9VylF3DbEeJbtdbih3lqpNXBw@Non#bi")
                contentType(Application.FormUrlEncoded)
                setBody("grant_type=urn%3Aietf%3Aparams%3Aoauth%3Agrant-type%3Ajwt-bearer&assertion=$assertion")
            }

            val body = response.body<Map<String, String>>()

            assertSoftly {
                response.status shouldBe OK
                body shouldContain ("access_token" to "$accessToken")
                body shouldContain ("refresh_token" to "$refreshToken")
                body shouldContain ("token_type" to "bearer")
                body shouldContain ("expires_in" to "5")
                body shouldContain ("scope" to "openid")
                body shouldContain ("state" to "$state")
            }
        }
    }

    // TODO - What about internal server errors?
}