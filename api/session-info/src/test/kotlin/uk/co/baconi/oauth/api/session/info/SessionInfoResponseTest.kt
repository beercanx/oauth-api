package uk.co.baconi.oauth.api.session.info

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import uk.co.baconi.oauth.api.common.authentication.AuthenticatedSession
import uk.co.baconi.oauth.api.common.authentication.AuthenticatedUsername
import uk.co.baconi.oauth.api.common.client.ClientId
import uk.co.baconi.oauth.api.session.info.SessionInfoResponse.Tokens
import uk.co.baconi.oauth.api.session.info.SessionInfoResponse.Token
import java.time.Instant

class SessionInfoResponseTest {

    private val json = Json {
        encodeDefaults = true
    }

    @Test
    fun `should serialize with no session or tokens`() {
        json.encodeToString(SessionInfoResponse(null, null)) shouldBeEqual """{"session":null,"tokens":null}"""
    }

    @Test
    fun `should serialize with an authenticated session`() {

        val authenticatedSession = AuthenticatedSession(AuthenticatedUsername("aardvark"))
        val sessionInfoResponse = SessionInfoResponse(authenticatedSession, null)

        assertSoftly(json.encodeToString(sessionInfoResponse)) {
            shouldContain(""""session":{"username":"aardvark"}""")
            shouldContain(""""tokens":null""")
        }
    }

    @Test
    fun `should serialize with empty tokens`() {

        val tokens = Tokens(emptyList(), emptyList(), emptyList())
        val sessionInfoResponse = SessionInfoResponse(null, tokens)

        assertSoftly(json.encodeToString(sessionInfoResponse)) {
            shouldContain(""""session":null""")
            shouldContain(""""tokens":{"authorisations":[],"accessTokens":[],"refreshTokens":[]}""")
        }
    }

    @Test
    fun `should serialize with tokens`() {

        val dateTime = "2024-05-06T14:43:32Z"
        val now = Instant.parse(dateTime)
        val token = Token(ClientId("aardvark"), now, now)
        val tokens = Tokens(listOf(token), listOf(token), listOf(token))
        val sessionInfoResponse = SessionInfoResponse(null, tokens)

        assertSoftly(json.encodeToString(sessionInfoResponse)) {
            shouldContain(""""session":null""")
            shouldContain(""""authorisations":[{"clientId":"aardvark","issuedAt":"$dateTime","expiresAt":"$dateTime"}]""")
            shouldContain(""""accessTokens":[{"clientId":"aardvark","issuedAt":"$dateTime","expiresAt":"$dateTime"}]""")
            shouldContain(""""refreshTokens":[{"clientId":"aardvark","issuedAt":"$dateTime","expiresAt":"$dateTime"}]""")
        }
    }

    @Test
    fun `should support deserializing no session or tokens`() {

        assertSoftly(json.decodeFromString<SessionInfoResponse>("""{"session":null,"tokens":null}""")) {
            shouldBeInstanceOf<SessionInfoResponse>()
            session.shouldBeNull()
            tokens.shouldBeNull()
        }
    }

    @Test
    fun `should support deserializing a session and empty tokens`() {

        assertSoftly(json.decodeFromString<SessionInfoResponse>("""
            |{
            |  "session": {
            |    "username": "aardvark"
            |  },
            |  "tokens": {
            |    "authorisations": [], 
            |    "accessTokens": [], 
            |    "refreshTokens": []
            |  }
            |}""".trimMargin())
        ) {
            shouldBeInstanceOf<SessionInfoResponse>()

            session shouldBe AuthenticatedSession(AuthenticatedUsername("aardvark"))

            assertSoftly(tokens.shouldNotBeNull()) {
                authorisations.shouldBeEmpty()
                accessTokens.shouldBeEmpty()
                refreshTokens.shouldBeEmpty()
            }
        }
    }

    @Test
    fun `should fail deserializing a session and some tokens because ClientId restrictions`() {

        assertThrows<SerializationException>("Deserialize unsupported for ClientId") {
            json.decodeFromString<SessionInfoResponse>("""
            |{
            |  "session": {
            |    "username": "aardvark"
            |  },
            |  "tokens":{
            |    "authorisations": [
            |       { "clientId":"aardvark1", "issuedAt":"2024-05-06T14:43:32Z", "expiresAt":"2024-05-06T14:43:32Z" }
            |    ],
            |    "accessTokens": [
            |      { "clientId":"aardvark2", "issuedAt":"2024-05-06T14:43:32Z", "expiresAt":"2024-05-06T14:43:32Z" }
            |    ], 
            |    "refreshTokens": [
            |      { "clientId":"aardvark3", "issuedAt":"2024-05-06T14:43:32Z", "expiresAt":"2024-05-06T14:43:32Z" }
            |    ]
            |  }
            |}""".trimMargin())
        }
    }
}