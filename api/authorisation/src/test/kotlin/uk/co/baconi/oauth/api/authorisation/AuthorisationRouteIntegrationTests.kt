package uk.co.baconi.oauth.api.authorisation

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.maps.shouldNotContainKey
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldStartWith
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.ContentType.*
import io.ktor.http.HttpHeaders.Location
import io.ktor.http.HttpStatusCode.Companion.Found
import io.ktor.http.HttpStatusCode.Companion.MethodNotAllowed
import io.ktor.http.HttpStatusCode.Companion.MovedPermanently
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.testing.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import uk.co.baconi.oauth.api.common.CommonModule.common
import uk.co.baconi.oauth.api.common.authentication.AuthenticatedSession
import uk.co.baconi.oauth.api.common.authentication.AuthenticatedUsername
import uk.co.baconi.oauth.api.common.authorisation.AuthorisationCodeRepository
import uk.co.baconi.oauth.api.common.authorisation.AuthorisationCodeTable
import uk.co.baconi.oauth.api.common.client.ClientConfigurationRepository
import java.net.URL
import java.net.URLDecoder.decode
import kotlin.text.Charsets.UTF_8

class AuthorisationRouteIntegrationTests : AuthorisationRoute {

    companion object {

        private const val AUTHORISE_ENDPOINT = "/authorise"
        private const val AUTHENTICATED_ENDPOINT = "/authenticated"

        private val database = Database.connect(
            url = "jdbc:h2:mem:AuthorisationRouteIntegrationTests;DB_CLOSE_DELAY=60;",
            driver = "org.h2.Driver"
        )

        init {
            transaction(database) { SchemaUtils.create(AuthorisationCodeTable) }
        }
    }

    override val authorisationCodeService = AuthorisationCodeService(AuthorisationCodeRepository(database))
    override val clientConfigurationRepository = ClientConfigurationRepository()

    private fun setupApplication(block: suspend ApplicationTestBuilder.(HttpClient) -> Unit) {
        testApplication {
            application {
                common()
            }
            routing {
                authorisation() // underTest
                route(AUTHENTICATED_ENDPOINT) {
                    get {
                        call.sessions.set(AuthenticatedSession(AuthenticatedUsername("aardvark")))
                    }
                }
            }
            block(createClient {
                followRedirects = false
            })
        }
    }

    @Nested
    inner class InvalidHttpRequest {

        @ParameterizedTest
        @CsvSource("POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        fun `only support get and head requests`(method: String) = setupApplication { client ->

            val response = client.request(AUTHORISE_ENDPOINT) {
                this.method = HttpMethod.parse(method)
            }

            response.status shouldBe MethodNotAllowed
        }
    }

    @Nested
    inner class InvalidClientIdentification {

        @Test
        fun `render a html page on invalid redirect`() = setupApplication { client ->

            val response = client.get {
                url {
                    takeFrom(AUTHORISE_ENDPOINT)
                    parameters["client_id"] = "with-no-redirect-uris"
                }
            }

            assertSoftly(response) {
                status shouldBe HttpStatusCode.BadRequest
                contentType() shouldBe Text.Html.withCharset(UTF_8)
                bodyAsText()
                    .shouldContain("Invalid Request")
                    .shouldContain("Invalid client or redirect was used.")
            }
        }

        @Test
        fun `render a html page on invalid client`() = setupApplication { client ->

            val response = client.get {
                url {
                    takeFrom(AUTHORISE_ENDPOINT)
                    parameters["client_id"] = "no-such-client"
                    parameters["redirect_uri"] = "https://no-such-client"
                }
            }

            assertSoftly(response) {
                status shouldBe HttpStatusCode.BadRequest
                contentType() shouldBe Text.Html.withCharset(UTF_8)
                bodyAsText()
                    .shouldContain("Invalid Request")
                    .shouldContain("Invalid client or redirect was used.")
            }
        }
    }

    @Nested
    inner class InvalidRequest {

        @Test
        fun `redirect back to redirect uri with an error when the rest of the request is invalid`() = setupApplication { client ->

            val response = client.get {
                url {
                    takeFrom(AUTHORISE_ENDPOINT)
                    parameters["client_id"] = "with-authorise-action"
                    parameters["redirect_uri"] = "https://redirect.baconi.co.uk"
                    parameters["state"] = "abc123"
                }
            }

            assertSoftly {
                response.status shouldBe Found

                val parameters = response.headers[Location]
                    .shouldStartWith("https://redirect.baconi.co.uk")
                    ?.asUrl()
                    ?.extractQueryParameters()

                parameters.shouldNotBeNull()
                parameters shouldContain ("error" to "invalid_request")
                parameters shouldContain ("error_description" to "missing parameter: response_type")
                parameters shouldContain ("state" to "abc123")
            }
        }
    }

    @Nested
    inner class ValidRequest {

        @Test
        fun `render a HTML page when not authenticated`() = setupApplication { client ->

            val response = client.get {
                url {
                    takeFrom(AUTHORISE_ENDPOINT)
                    parameters["client_id"] = "with-authorise-action"
                    parameters["redirect_uri"] = "https://redirect.baconi.co.uk"
                    parameters["response_type"] = "code"
                    parameters["scope"] = "basic"
                    parameters["state"] = "7d9736a2-92ed-4a95-a83d-8adf4d2da0ee"
                }
            }

            assertSoftly(response) {
                status shouldBe OK
                contentType() shouldBe Text.Html.withCharset(UTF_8)
                bodyAsText()
                    .shouldContain("<title>Login Page</title>")
                    .shouldContain("""<script defer="defer" src="http://localhost:8081/assets/js/authentication.js"></script>""")
            }
        }

        @Test
        fun `redirect back to redirect uri with a code when authenticated`() = setupApplication { client ->

            val response = client.get {

                setupAuthenticatedSession(client)

                url {
                    takeFrom(AUTHORISE_ENDPOINT)
                    parameters["client_id"] = "with-authorise-action"
                    parameters["redirect_uri"] = "https://redirect.baconi.co.uk"
                    parameters["response_type"] = "code"
                    parameters["scope"] = "basic"
                    parameters["state"] = "7d9736a2-92ed-4a95-a83d-8adf4d2da0ee"
                }
            }

            assertSoftly {
                response.status shouldBe Found

                val parameters = response.headers[Location]
                    .shouldStartWith("https://redirect.baconi.co.uk")
                    ?.asUrl()
                    ?.extractQueryParameters()

                parameters.shouldNotBeNull()
                parameters shouldContainKey "code"
                parameters shouldContain ("state" to "7d9736a2-92ed-4a95-a83d-8adf4d2da0ee")
                parameters shouldNotContainKey "error"
                parameters shouldNotContainKey "error_description"
            }
        }
    }

    private fun String.asUrl() = URL(this)

    private fun URL.extractQueryParameters(): Map<String, String> {
        val query = query?.split("&")?.associate { it.split("=").let { (key, value) -> key to decode(value, UTF_8) } }
        query.shouldNotBeNull()
        return query
    }

    private suspend fun HttpMessageBuilder.setupAuthenticatedSession(client: HttpClient) {
        client.get(AUTHENTICATED_ENDPOINT).setCookie().forEach { cookie ->
            cookie(cookie.name, cookie.value, domain = cookie.domain, path = cookie.path)
        }
    }
}