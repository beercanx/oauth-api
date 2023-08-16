package uk.co.baconi.oauth.api.token

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import io.kotest.matchers.throwable.shouldHaveMessage
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import uk.co.baconi.oauth.api.token.TokenErrorType.InvalidClient
import uk.co.baconi.oauth.api.token.TokenErrorType.InvalidRequest
import java.util.UUID

class TokenResponseTests {

    private val json = Json {
        encodeDefaults = true
    }

    @Nested
    inner class Success {

        private val accessToken = UUID.randomUUID()
        private val refreshToken = UUID.randomUUID()
        private val state = UUID.randomUUID()

        private val success = TokenResponse.Success(
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiresIn = 1,
            scope = emptySet(),
            state = state.toString()
        )

        @Test
        fun `should not include sensitive values in toString`() {

            assertSoftly("$success") {
                shouldContain("accessToken='REDACTED'")
                shouldNotContain("$accessToken")
                shouldContain("refreshToken='REDACTED'")
                shouldNotContain("$refreshToken")
                shouldContain("state='REDACTED'")
                shouldNotContain("$state")
            }
        }

        @Test
        fun `should include sensitive values in serialisation`() {

            assertSoftly(json.encodeToString(success)) {
                shouldContain(""""access_token":"$accessToken"""")
                shouldContain(""""refresh_token":"$refreshToken"""")
                shouldContain(""""state":"$state"""")
            }
        }
    }

    @Nested
    inner class Failed {

        @Test
        fun `should not have an empty description`() {

            assertThrows<IllegalArgumentException> {
                TokenResponse.Failed(
                    error = InvalidClient,
                    errorDescription = " "
                )
            } shouldHaveMessage "Error description should not be blank!"
        }

        @Test
        fun `should always serialise error and description`() {

            val invalid = TokenResponse.Failed(
                error = InvalidRequest,
                errorDescription = "something invalid"
            )

            json.encodeToString(invalid) shouldBe """{"error":"invalid_request","error_description":"something invalid"}"""
        }
    }
}