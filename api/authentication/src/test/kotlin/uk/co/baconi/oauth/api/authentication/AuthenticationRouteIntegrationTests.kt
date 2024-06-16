package uk.co.baconi.oauth.api.authentication

import de.mkammerer.argon2.Argon2Factory
import de.mkammerer.argon2.Argon2Factory.Argon2Types.ARGON2id
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.beEmpty
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.MethodNotAllowed
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.http.HttpStatusCode.Companion.UnsupportedMediaType
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import uk.co.baconi.oauth.api.common.CommonModule.common
import uk.co.baconi.oauth.api.common.authentication.*
import uk.co.baconi.oauth.api.common.authentication.CustomerState.*
import java.util.*

class AuthenticationRouteIntegrationTests : AuthenticationRoute {

    companion object {

        private val database = Database.connect(
            url = "jdbc:h2:mem:AuthenticationRouteIntegrationTest;DB_CLOSE_DELAY=60;",
            driver = "org.h2.Driver"
        )

        private const val AUTHENTICATION_ENDPOINT = "/authentication"
        private const val SESSION_ENDPOINT = "/authentication/session"

        private const val ACTIVE_CUSTOMER = "aardvark"
        private const val LOCKED_CUSTOMER = "badger"
        private const val CLOSED_CUSTOMER = "cicada"
        private const val SUSPENDED_CUSTOMER = "dodo"

        private val customerCredentialRepository = CustomerCredentialRepository(database)
        private val customerStatusRepository = CustomerStatusRepository(database)

        init {
            transaction(database) { SchemaUtils.create(CustomerCredentialTable) }
            transaction(database) { SchemaUtils.create(CustomerStatusTable) }

            val hash = Argon2Factory.create(ARGON2id).hash(2, 16, 1, "121212".toCharArray())

            listOf(
                ACTIVE_CUSTOMER to Active,
                LOCKED_CUSTOMER to Locked,
                CLOSED_CUSTOMER to Closed,
                SUSPENDED_CUSTOMER to Suspended
            ).forEach { (username, state) ->
                CustomerCredential(username, hash).also(customerCredentialRepository::insert)
                CustomerStatus(username, state).also(customerStatusRepository::insert)
            }
        }
    }

    override val customerAuthenticationService = CustomerAuthenticationService(
        customerCredentialRepository, customerStatusRepository
    )

    private fun setupApplication(block: suspend ApplicationTestBuilder.(HttpClient) -> Unit) {
        testApplication {
            application {
                common()
            }
            routing {
                authentication() // underTest
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

    private fun HttpRequestBuilder.authenticationRequest(
        method: HttpMethod = HttpMethod.Post,
        accept: ContentType = ContentType.Application.Json,
        contentType: ContentType = ContentType.Application.Json,
        body: String? = null,
    ) {
        this.method = method
        accept(accept)
        contentType(contentType)
        setBody(body)
    }

    @Nested
    inner class SessionEndpoint {

        @ParameterizedTest
        @CsvSource("POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        fun `should only support get requests`(method: String) = setupApplication { client ->

            val response = client.request(SESSION_ENDPOINT) {
                authenticationRequest(method = HttpMethod.parse(method))
            }

            assertSoftly(response) {
                status shouldBe MethodNotAllowed
                bodyAsText() should beEmpty()
            }
        }
    }

    @Nested
    inner class InvalidHttpRequest {

        @ParameterizedTest
        @CsvSource("GET", "PUT", "PATCH", "DELETE", "HEAD", "OPTIONS")
        fun `should only support post requests`(method: String) = setupApplication { client ->

            val response = client.request(AUTHENTICATION_ENDPOINT) {
                authenticationRequest(method = HttpMethod.parse(method))
            }

            assertSoftly(response) {
                status shouldBe MethodNotAllowed
                bodyAsText() should beEmpty()
            }
        }

        @ParameterizedTest
        @CsvSource("""x-www-form-urlencoded,username=aardvark""", """xml,<auth><username>aardvark</username></auth>""")
        fun `should only support json post requests`(type: String, body: String) = setupApplication { client ->

            val response = client.request(AUTHENTICATION_ENDPOINT) {
                authenticationRequest(contentType = ContentType("application", type), body = body)
            }

            assertSoftly(response) {
                status shouldBe UnsupportedMediaType
                bodyAsText() should beEmpty()
            }
        }
    }

    @Nested
    inner class InvalidBodyContent {

        @Test
        fun `should return bad request when an invalid csrf token is used`() = setupApplication { client ->

            val response = client.request(AUTHENTICATION_ENDPOINT) {

                setupCsrfToken(client)

                authenticationRequest(body = """{"username":"aardvark","password":[1,2,1,2,1,2],"csrfToken":"bad"}""")
            }

            assertSoftly(response) {
                status shouldBe BadRequest
                bodyAsText() shouldBe """{"type":"failure"}"""
            }
        }

        @Test
        fun `should return bad request when username is missing`() = setupApplication { client ->

            val response = client.request(AUTHENTICATION_ENDPOINT) {

                val csrfToken = setupCsrfToken(client)

                authenticationRequest(body = """{"password":[1,2,1,2,1,2],"csrfToken":"$csrfToken"}""")
            }

            assertSoftly(response) {
                status shouldBe BadRequest
                bodyAsText() shouldBe """{"type":"failure"}"""
            }
        }

        @Test
        fun `should return bad request when password is missing`() = setupApplication { client ->

            val response = client.request(AUTHENTICATION_ENDPOINT) {

                val csrfToken = setupCsrfToken(client)

                authenticationRequest(body = """{"username":"aardvark","csrfToken":"$csrfToken"}""")
            }

            assertSoftly(response) {
                status shouldBe BadRequest
                bodyAsText() shouldBe """{"type":"failure"}"""
            }
        }
    }

    @Nested
    inner class Authentications {

        @ParameterizedTest
        @CsvSource("ermine,121212", "aardvark,212121", "badger,121212", "cicada,121212", "dodo,121212")
        fun `should return unauthorized when not okay`(username: String, password: String) = setupApplication { client ->

            val pins = password.toCharArray().contentToString()

            val response = client.request(AUTHENTICATION_ENDPOINT) {

                val csrfToken = setupCsrfToken(client)

                authenticationRequest(body = """{"username":"$username", "password": $pins, "csrfToken": "$csrfToken"}""")
            }

            assertSoftly(response) {
                status shouldBe Unauthorized
                bodyAsText() shouldBe """{"type":"failure"}"""
            }
        }

        @Test
        fun `should return OK when okay`() = setupApplication { client ->

            val response = client.request(AUTHENTICATION_ENDPOINT) {

                val csrfToken = setupCsrfToken(client)

                authenticationRequest(body = """{"username":"aardvark","password":[1,2,1,2,1,2],"csrfToken":"$csrfToken"}""")
            }

            assertSoftly(response) {
                status shouldBe OK
                bodyAsText() shouldBe """{"type":"success","username":"aardvark"}"""
            }
        }
    }

    private suspend fun HttpMessageBuilder.setupCsrfToken(client: HttpClient): String {
        return client.get(SESSION_ENDPOINT).apply {
            setCookie().forEach { cookie ->
                cookie(cookie.name, cookie.value, domain = cookie.domain, path = cookie.path)
            }
        }.body<JsonObject>()["csrfToken"]!!.jsonPrimitive.content
    }
}