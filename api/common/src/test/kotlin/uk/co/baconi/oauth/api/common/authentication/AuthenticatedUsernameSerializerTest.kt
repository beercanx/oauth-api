package uk.co.baconi.oauth.api.common.authentication

import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.common.authentication.AuthenticatedUsername
import uk.co.baconi.oauth.common.authentication.AuthenticatedUsernameSerializer

class AuthenticatedUsernameSerializerTest {

    private val underTest = AuthenticatedUsernameSerializer

    @Nested
    inner class Encode {

        @Test
        fun `should be able to encode an authenticated username`() {
            Json.encodeToString(underTest, AuthenticatedUsername("aardvark")) shouldBe "\"aardvark\""
        }
    }

    @Nested
    inner class Decode {

        @Test
        fun `should be able to decode an authenticated username`() {
            Json.decodeFromString(underTest, "\"aardvark\"") shouldBe AuthenticatedUsername("aardvark")
        }
    }
}