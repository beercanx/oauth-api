package uk.co.baconi.oauth.api.common.authentication

import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class AuthenticatedSessionSerializerTest {

    private val underTest = AuthenticatedSession.serializer()
    private val session = AuthenticatedSession(AuthenticatedUsername("aardvark"))

    @Nested
    inner class Encode {

        @Test
        fun `should be able to encode an authenticated session`() {
            Json.encodeToString(underTest, session) shouldBe """{"username":"aardvark"}"""
        }
    }

    @Nested
    inner class Decode {

        @Test
        fun `should be able to decode an authenticated session`() {
            Json.decodeFromString(underTest, """{"username":"aardvark"}""") shouldBe session
        }
    }
}