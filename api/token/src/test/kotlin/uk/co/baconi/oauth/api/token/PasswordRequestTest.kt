package uk.co.baconi.oauth.api.token

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import io.mockk.mockk
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class PasswordRequestTest {

    @Nested
    inner class ToString {

        @Test
        fun `should not include password value`() {
            assertSoftly(
                PasswordRequest(
                    principal = mockk(),
                    scopes = emptySet(),
                    username = "aardvark",
                    password = "badger",
                ).toString()
            ) {
                shouldNotContain("badger")
                shouldContain("password='REDACTED'")
            }
        }
    }
}