package uk.co.baconi.oauth.api.user.info

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.matchers.maps.beEmpty
import io.kotest.matchers.maps.haveSize
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.maps.shouldNotContainKey
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.string.beBlank
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.HttpMethod.Companion.Delete
import io.ktor.http.HttpMethod.Companion.Get
import io.ktor.http.HttpMethod.Companion.Options
import io.ktor.http.HttpMethod.Companion.Patch
import io.ktor.http.HttpMethod.Companion.Post
import io.ktor.http.HttpMethod.Companion.Put
import io.ktor.http.HttpStatusCode.Companion.Forbidden
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import io.ktor.util.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.api.common.AuthenticationModule
import uk.co.baconi.oauth.api.common.CommonModule.common
import uk.co.baconi.oauth.api.common.client.ClientConfigurationRepository
import uk.co.baconi.oauth.api.common.client.ClientSecretRepository
import uk.co.baconi.oauth.api.common.client.ClientSecretService
import uk.co.baconi.oauth.api.common.scope.ScopeConfigurationRepository
import uk.co.baconi.oauth.api.common.token.AccessToken
import uk.co.baconi.oauth.api.common.token.AccessTokenRepository
import uk.co.baconi.oauth.api.common.token.AccessTokenService
import uk.co.baconi.oauth.api.common.token.AccessTokenTable
import java.time.Instant
import java.time.temporal.ChronoUnit

private const val USERINFO_ENDPOINT = "/userinfo"

class UserInfoIntegrationTests : AuthenticationModule, UserInfoRoute {

    companion object {

        private val database = Database.connect(
            url = "jdbc:h2:mem:UserInfoIntegrationTests;DB_CLOSE_DELAY=60;",
            driver = "org.h2.Driver"
        )

        private val accessTokenRepository = AccessTokenRepository(database)

        private val activeToken = accessToken()
        private val noScopesToken = accessToken(scopes = emptySet())
        private val futureToken = accessToken(now = Instant.now().plus(10, ChronoUnit.DAYS))
        private val expiredToken = accessToken(now = Instant.now().minus(10, ChronoUnit.DAYS))

        init {
            transaction(database) { SchemaUtils.create(AccessTokenTable) }
            accessTokenRepository.insert(activeToken)
            accessTokenRepository.insert(noScopesToken)
            accessTokenRepository.insert(futureToken)
            accessTokenRepository.insert(expiredToken)
        }
    }

    override val clientSecretService = ClientSecretService(ClientSecretRepository(), ClientConfigurationRepository())
    override val accessTokenService = AccessTokenService(accessTokenRepository)
    override val userInfoService = UserInfoService(ScopeConfigurationRepository())

    private fun setupApplication(block: suspend ApplicationTestBuilder.(HttpClient) -> Unit) {
        testApplication {
            application {
                common()
                authentication()
            }
            routing {
                userInfo() // underTest
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

    private fun HttpRequestBuilder.userInfoRequest(
        accessToken: AccessToken = activeToken,
        method: HttpMethod = Get,
        accept: ContentType = ContentType.Application.Json,
        authorisation: HttpMessageBuilder.() -> Unit = { bearerAuth("${accessToken.value}") }
    ) {
        this.method = method
        accept(accept)
        setBody(body)
        authorisation()
    }

    @Nested
    inner class InvalidHttpRequest {

        @Test
        fun `should only support get and head requests`() = setupApplication { client ->
            assertSoftly {
                for (method in listOf(Post, Put, Patch, Delete, Options)) {
                    withClue(method) {

                        val response = client.request(USERINFO_ENDPOINT) {
                            userInfoRequest(method = method)
                        }

                        response.status shouldBe HttpStatusCode.MethodNotAllowed
                    }
                }
            }
        }
    }

    @Nested
    inner class InvalidAuthentication {

        @Test
        fun `should return unauthorised response when authentication is missing`() = setupApplication { client ->

            val response = client.request(USERINFO_ENDPOINT) {
                userInfoRequest {
                    // No authorization header
                }
            }

            assertSoftly(response) {
                status shouldBe Unauthorized
                bodyAsText() should beBlank()
                it.headers.toMap() shouldContainKey "WWW-Authenticate"
                it.headers["WWW-Authenticate"] shouldBe "Bearer realm=oauth-api"
            }
        }

        @Test
        fun `should return unauthorised response when an expired access token is used`() = setupApplication { client ->

            val response = client.request(USERINFO_ENDPOINT) {
                userInfoRequest(expiredToken)
            }

            assertSoftly(response) {
                status shouldBe Unauthorized
                bodyAsText() should beBlank()
                it.headers.toMap() shouldContainKey "WWW-Authenticate"
                it.headers["WWW-Authenticate"] shouldBe "Bearer realm=oauth-api"
            }
        }

        @Test
        fun `should return forbidden response when using a token with no openid scope`() = setupApplication { client ->

            val response = client.request(USERINFO_ENDPOINT) {
                userInfoRequest(noScopesToken)
            }

            assertSoftly(response) {
                status shouldBe Forbidden
                bodyAsText() should beBlank()
                it.headers.toMap() shouldContainKey "WWW-Authenticate"
                it.headers["WWW-Authenticate"] shouldBe "Bearer realm=oauth-api"
            }
        }
    }

    @Nested
    inner class InvalidBodyContent {
        // TODO - Implement that it shouldn't receive a body
        // TODO - Should it reject if there's a content type, indicating there's a body on the GET request?
    }

    @Nested
    inner class Claims {

        @Test
        fun `should return subject when the claim is issued`() = setupApplication { client ->

            val response = client.request(USERINFO_ENDPOINT) {
                userInfoRequest(activeToken)
            }

            assertSoftly(response) {
                status shouldBe OK
                it.headers.toMap() shouldNotContainKey "WWW-Authenticate"
                body<Map<String, String?>>().apply {
                    this shouldNot beEmpty()
                    this should haveSize(1)
                    this["sub"] shouldBe "aardvark"
                }
            }
        }

        // TODO - Other claims as and when they are implemented.

    }
}