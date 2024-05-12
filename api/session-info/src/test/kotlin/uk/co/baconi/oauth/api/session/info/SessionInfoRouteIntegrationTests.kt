package uk.co.baconi.oauth.api.session.info

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.json.*
import io.kotest.assertions.json.schema.array
import io.kotest.assertions.json.schema.jsonSchema
import io.kotest.assertions.json.schema.obj
import io.kotest.assertions.json.schema.shouldMatchSchema
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.ContentType.*
import io.ktor.http.ContentType.Application
import io.ktor.http.HttpStatusCode.Companion.MethodNotAllowed
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.http.HttpStatusCode.Companion.UnsupportedMediaType
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.testing.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import uk.co.baconi.oauth.api.common.AuthenticationModule
import uk.co.baconi.oauth.api.common.CommonModule.common
import uk.co.baconi.oauth.api.common.authentication.AuthenticatedSession
import uk.co.baconi.oauth.api.common.authentication.AuthenticatedUsername
import uk.co.baconi.oauth.api.common.authorisation.AuthorisationCode
import uk.co.baconi.oauth.api.common.authorisation.AuthorisationCodeRepository
import uk.co.baconi.oauth.api.common.authorisation.AuthorisationCodeTable
import uk.co.baconi.oauth.api.common.client.ClientConfigurationRepository
import uk.co.baconi.oauth.api.common.client.ClientId
import uk.co.baconi.oauth.api.common.client.ClientSecretRepository
import uk.co.baconi.oauth.api.common.client.ClientSecretService
import uk.co.baconi.oauth.api.common.scope.ScopeRepository
import uk.co.baconi.oauth.api.common.token.*
import java.net.URLEncoder.encode
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.text.Charsets.UTF_8

class SessionInfoRouteIntegrationTests : AuthenticationModule, SessionInfoRoute {

    companion object {

        private const val SESSION_ENDPOINT = "/session"
        private const val SESSION_INFO_ENDPOINT = "/session/info"

        private val database = Database.connect(
            url = "jdbc:h2:mem:SessionInfoRouteIntegrationTests;DB_CLOSE_DELAY=60;",
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

    private val scopeRepository = ScopeRepository()
    private val clientSecretRepository = ClientSecretRepository()
    private val clientConfigurationRepository = ClientConfigurationRepository(scopeRepository)

    private val accessTokenRepository = AccessTokenRepository(database)
    private val refreshTokenRepository = RefreshTokenRepository(database)
    private val refreshTokenService = RefreshTokenService(refreshTokenRepository)
    private val authorisationCodeRepository = AuthorisationCodeRepository(database)

    override val clientSecretService = ClientSecretService(clientSecretRepository, clientConfigurationRepository)
    override val accessTokenService = AccessTokenService(accessTokenRepository)
    override val sessionInfoService = SessionInfoService(accessTokenRepository, refreshTokenRepository, authorisationCodeRepository)

    private fun setupApplication(jsonClient: Boolean = false, block: suspend ApplicationTestBuilder.(HttpClient) -> Unit) {
        testApplication {
            application {
                common()
            }
            routing {
                sessionInfo() // underTest
                route("/create-authenticated-session/{username}") {
                    get {
                        val username = checkNotNull(call.parameters["username"]) { "username was null" }
                        call.sessions.set(AuthenticatedSession(AuthenticatedUsername(username)))
                    }
                }
            }
            block(
                createClient {
                    followRedirects = false
                    if(jsonClient) {
                        install(ContentNegotiation) {
                            json()
                        }
                    }
                }
            )
        }
    }

    /**
     * Hits the fake authenticate endpoint and copy the cookies into the request builder.
     */
    private suspend fun HttpMessageBuilder.withAuthenticatedSession(client: HttpClient, username: String) {
        client.get("/create-authenticated-session/${encode(username, UTF_8)}").setCookie().forEach { cookie ->
            cookie(cookie.name, cookie.value, domain = cookie.domain, path = cookie.path)
        }
    }

    @Nested
    inner class InvalidHttpRequest {

        @ParameterizedTest
        @CsvSource(
            "$SESSION_ENDPOINT,POST", "$SESSION_ENDPOINT,PUT", "$SESSION_ENDPOINT,PATCH", "$SESSION_ENDPOINT,DELETE", "$SESSION_ENDPOINT,OPTIONS",
            "$SESSION_INFO_ENDPOINT,POST", "$SESSION_INFO_ENDPOINT,PUT", "$SESSION_INFO_ENDPOINT,PATCH", "$SESSION_INFO_ENDPOINT,DELETE", "$SESSION_INFO_ENDPOINT,OPTIONS",
        )
        fun `should reject non get and head requests`(path: String, method: String) = setupApplication { client ->

            val response = client.request(path) {
                this.method = HttpMethod.parse(method)
            }

            response.status shouldBe MethodNotAllowed
        }

        @ParameterizedTest
        @CsvSource(
            "$SESSION_ENDPOINT,application/json", "$SESSION_ENDPOINT,application/xml",
            "$SESSION_INFO_ENDPOINT,text/html", "$SESSION_INFO_ENDPOINT,application/xml"
        )
        fun `should reject unsupported accept types`(path: String, type: String) = setupApplication { client ->

            val response = client.get(path) {
                this.accept(ContentType.parse(type))
            }

            response.status shouldBe UnsupportedMediaType
        }
    }

    @Nested
    inner class ValidHttpRequest {

        @Test
        fun `session info should return NO session data when NOT authenticated`() = setupApplication(jsonClient = true) { client ->

            val response = client.get(SESSION_INFO_ENDPOINT)

            assertSoftly(response) {
                status shouldBe OK
                contentType() shouldBe Application.Json.withCharset(UTF_8)
                body<String>() shouldEqualJson "{}"
            }
        }

        @Test
        fun `session info should return session data when authenticated`() = setupApplication(jsonClient = true) { client ->

            val now = Instant.now()
            val username = "aardvark"
            val aardvark = AuthenticatedUsername(username)
            authorisationCodeRepository.insert(AuthorisationCode.Basic(
                value = UUID.randomUUID(),
                username = aardvark,
                clientId = ClientId("badger"),
                scopes = setOf(),
                used = false,
                state = UUID.randomUUID().toString(),
                issuedAt = now,
                expiresAt = now.plus(1, ChronoUnit.HOURS),
                redirectUri = "https://aardvark.baconi.co.uk",
            ))
            accessTokenService.issue(aardvark, ClientId("duck"), setOf())
            refreshTokenService.issue(aardvark, ClientId("emperor-penguin"), setOf())

            val response = client.get(SESSION_INFO_ENDPOINT) {
                withAuthenticatedSession(client, username)
            }

            assertSoftly(response) {
                status shouldBe OK
                contentType() shouldBe Application.Json.withCharset(UTF_8)
                body<String>() shouldEqualSpecifiedJson """
                    |{
                    |  "session": { 
                    |    "username": "aardvark" 
                    |  },
                    |  "tokens":{
                    |    "authorisations": [ 
                    |      { "clientId":"badger" } 
                    |    ],
                    |    "accessTokens": [ 
                    |      { "clientId":"duck" } 
                    |    ], 
                    |    "refreshTokens": [ 
                    |      { "clientId":"emperor-penguin" } 
                    |    ]
                    |  }
                    |}""".trimMargin()
            }
        }

        @Test
        fun `session should display NO session data when NOT authenticated`() = setupApplication { client ->

            val response = client.get(SESSION_ENDPOINT)

            assertSoftly(response) {
                status shouldBe OK
                contentType() shouldBe Text.Html.withCharset(UTF_8)
                val body = body<String>()
                body shouldContain "<title>Session Info</title>"
                body shouldContain "<h1>Session Info</h1>"
                body shouldNotContain "<table>"
                body shouldNotContain "<caption>Authorisation Tokens</caption>"
                body shouldNotContain "<caption>Access Tokens</caption>"
                body shouldNotContain "<caption>Refresh Tokens</caption>"
            }
        }

        @Test
        fun `session should display session data when authenticated`() = setupApplication { client ->

            accessTokenService.issue(AuthenticatedUsername("aardvarks"), ClientId("badgers"), setOf())
            refreshTokenService.issue(AuthenticatedUsername("aardvarks"), ClientId("cicadas"), setOf())

            val response = client.get(SESSION_ENDPOINT) {
                withAuthenticatedSession(client, "aardvarks")
            }

            assertSoftly(response) {
                status shouldBe OK
                contentType() shouldBe Text.Html.withCharset(UTF_8)
                val body = body<String>()
                body shouldContain "<title>Session Info</title>"
                body shouldContain "<h1>Session Info</h1>"
                body shouldContain "<p>AuthenticatedUsername(value=aardvarks)</p>"
                body shouldContain "<table>"
                body shouldContain "<caption>Authorisation Tokens</caption>"
                body shouldContain "<caption>Access Tokens</caption>"
                body shouldContain "<td>ClientId(value=badgers)</td>"
                body shouldContain "<caption>Refresh Tokens</caption>"
                body shouldContain "<td>ClientId(value=cicadas)</td>"
            }
        }
    }
}