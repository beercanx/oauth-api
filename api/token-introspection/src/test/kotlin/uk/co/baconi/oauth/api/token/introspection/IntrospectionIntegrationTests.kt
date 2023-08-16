package uk.co.baconi.oauth.api.token.introspection

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.beBlank
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.HttpMethod.Companion.parse
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Forbidden
import io.ktor.http.HttpStatusCode.Companion.MethodNotAllowed
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.http.HttpStatusCode.Companion.UnsupportedMediaType
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import uk.co.baconi.oauth.api.common.AuthenticationModule
import uk.co.baconi.oauth.api.common.CommonModule.common
import uk.co.baconi.oauth.api.common.client.ClientConfigurationRepository
import uk.co.baconi.oauth.api.common.client.ClientSecretRepository
import uk.co.baconi.oauth.api.common.client.ClientSecretService
import uk.co.baconi.oauth.api.common.token.AccessTokenRepository
import uk.co.baconi.oauth.api.common.token.AccessTokenService
import uk.co.baconi.oauth.api.common.token.AccessTokenTable
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

class IntrospectionIntegrationTests : AuthenticationModule, IntrospectionRoute {

    companion object {

        private val database = Database.connect(
            url = "jdbc:h2:mem:IntrospectionRouteIntegrationTests;DB_CLOSE_DELAY=60;",
            driver = "org.h2.Driver"
        )

        private val accessTokenRepository = AccessTokenRepository(database)

        private const val introspectionEndpoint = "/introspect"
        private val activeToken = accessToken()
        private val missingToken = accessToken()
        private val futureToken = accessToken(now = Instant.now().plus(10, ChronoUnit.DAYS))
        private val expiredToken = accessToken(now = Instant.now().minus(10, ChronoUnit.DAYS))

        init {
            transaction(database) { SchemaUtils.create(AccessTokenTable) }
            accessTokenRepository.insert(activeToken)
            accessTokenRepository.insert(futureToken)
            accessTokenRepository.insert(expiredToken)
        }
    }

    override val clientSecretService = ClientSecretService(ClientSecretRepository(), ClientConfigurationRepository())
    override val accessTokenService = AccessTokenService(accessTokenRepository)
    override val introspectionService = IntrospectionService(accessTokenRepository)

    private fun setupApplication(block: suspend ApplicationTestBuilder.(HttpClient) -> Unit) {
        testApplication {
            application {
                common()
                authentication()
            }
            routing {
                introspection() // underTest
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

    private fun HttpRequestBuilder.introspectionRequest(
        token: UUID = UUID.randomUUID(),
        method: HttpMethod = HttpMethod.Post,
        accept: ContentType = ContentType.Application.Json,
        contentType: ContentType = ContentType.Application.FormUrlEncoded,
        body: String = "token=${token}",
        authorisation: HttpMessageBuilder.() -> Unit = { basicAuth("consumer-z", "7XLlyzjRpvICEkNrsgtOuuj1S30Bj9Xu") }
    ) {
        this.method = method
        accept(accept)
        contentType(contentType)
        setBody(body)
        authorisation()
    }

    @Nested
    inner class InvalidHttpRequest {

        @ParameterizedTest
        @CsvSource("GET", "PUT", "PATCH", "DELETE", "HEAD", "OPTIONS")
        fun `should only support post requests`(method: String) = setupApplication { client ->

            val response = client.request(introspectionEndpoint) {
                introspectionRequest(method = parse(method))
            }

            response.status shouldBe MethodNotAllowed
        }

        @Test
        fun `should only support url encoded form requests`() = setupApplication { client ->
            assertSoftly {
                for ((contentType, body) in listOf(
                    ContentType.Application.Json to """{"token": "${UUID.randomUUID()}"}""",
                    ContentType.Application.Xml to "<token>${UUID.randomUUID()}</token>"
                )) {
                    withClue(contentType) {

                        val response = client.request(introspectionEndpoint) {
                            introspectionRequest(contentType = contentType, body = body)
                        }

                        response.status shouldBe UnsupportedMediaType
                    }
                }
            }
        }
    }

    @Nested
    inner class InvalidClientAuthorisation {

        @Test
        fun `should return unauthorised response when a public client is used`() = setupApplication { client ->

            val response = client.request(introspectionEndpoint) {
                introspectionRequest {
                    setBody("token=${UUID.randomUUID()}&clientId=consumer-y")
                }
            }

            assertSoftly(response) {
                status shouldBe Unauthorized
                bodyAsText() should beBlank()
            }
        }

        @Test
        fun `should return unauthorised response when a confidential clients without the allowed action is used`() =
            setupApplication { client ->

                val response = client.request(introspectionEndpoint) {
                    introspectionRequest {
                        basicAuth("no-introspection", "9VylF3DbEeJbtdbih3lqpNXBw@Non#bi")
                    }
                }

                assertSoftly(response) {
                    status shouldBe Forbidden
                    assertSoftly(body<Map<String, String>>()) {
                        shouldContain("error" to "unauthorized_client")
                        shouldContain("error_description" to "client is not allowed to introspect")
                    }
                }
            }
    }

    @Nested
    inner class InvalidBodyContent {

        @Test
        fun `should return invalid request on missing token in request`() = setupApplication { client ->

            val response = client.request(introspectionEndpoint) {
                introspectionRequest(body = "")
            }

            assertSoftly(response) {
                status shouldBe BadRequest
                assertSoftly(body<Map<String, String>>()) {
                    shouldContain("error" to "invalid_request")
                    shouldContain("error_description" to "missing parameter: token")
                }
            }
        }

        @Test
        fun `should return invalid request on blank token`() = setupApplication { client ->

            val response = client.request(introspectionEndpoint) {
                introspectionRequest(body = "token=")
            }

            assertSoftly(response) {
                status shouldBe BadRequest
                assertSoftly(body<Map<String, String>>()) {
                    shouldContain("error" to "invalid_request")
                    shouldContain("error_description" to "invalid parameter: token")
                }
            }
        }

        @Test
        fun `should return invalid request on non uuid token`() = setupApplication { client ->

            val response = client.request(introspectionEndpoint) {
                introspectionRequest(body = "token=aardvark")
            }

            assertSoftly(response) {
                status shouldBe BadRequest
                assertSoftly(body<Map<String, String>>()) {
                    shouldContain("error" to "invalid_request")
                    shouldContain("error_description" to "invalid parameter: token")
                }
            }
        }
    }

    @Nested
    inner class TokenStates {

        @Test
        fun `should return an active response for an access token that is active`() = setupApplication { client ->

            val response = client.request(introspectionEndpoint) {
                introspectionRequest(body = "token=${activeToken.value}")
            }

            assertSoftly(response) {
                status shouldBe OK
                assertSoftly(body<Map<String, String>>()) {
                    shouldContain("active" to "true")
                    shouldContain("sub" to "aardvark")
                    shouldContain("scope" to "openid profile::read profile::write")
                    shouldContain("username" to "aardvark")
                    shouldContain("client_id" to "badger")
                    shouldContain("token_type" to "bearer")
                    shouldContain("exp" to "${activeToken.expiresAt.epochSecond}")
                    shouldContain("iat" to "${activeToken.issuedAt.epochSecond}")
                    shouldContain("nbf" to "${activeToken.notBefore.epochSecond}")
                }
            }
        }

        @Test
        fun `should return an inactive response for an access token that does not exist`() =
            setupApplication { client ->

                val response = client.request(introspectionEndpoint) {
                    introspectionRequest(body = "token=${missingToken.value}")
                }

                assertSoftly(response) {
                    status shouldBe OK
                    assertSoftly(body<Map<String, String>>()) {
                        shouldContain("active" to "false")
                    }
                }
            }

        @Test
        fun `should return an inactive response for an access token that has expired`() = setupApplication { client ->

            val response = client.request(introspectionEndpoint) {
                introspectionRequest(body = "token=${expiredToken.value}")
            }

            assertSoftly(response) {
                status shouldBe OK
                assertSoftly(body<Map<String, String>>()) {
                    shouldContain("active" to "false")
                }
            }
        }

        @Test
        fun `should return an inactive response for an access token that is in the future`() =
            setupApplication { client ->

                val response = client.request(introspectionEndpoint) {
                    introspectionRequest(body = "token=${futureToken.value}")
                }

                assertSoftly(response) {
                    status shouldBe OK
                    assertSoftly(body<Map<String, String>>()) {
                        shouldContain("active" to "false")
                    }
                }
            }
    }
}