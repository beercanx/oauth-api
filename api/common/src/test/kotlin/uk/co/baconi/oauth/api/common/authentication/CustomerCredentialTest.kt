package uk.co.baconi.oauth.api.common.authentication

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CustomerCredentialTest {

    @Nested
    inner class ToString {

        @Test
        fun `should not include customer credential secret`() {
            assertSoftly(
                CustomerCredential(
                    username = "aardvark",
                    hashedSecret = "hashed-secret",
                ).toString()
            ) {
                shouldNotContain("hashed-secret")
                shouldContain("hashedSecret='REDACTED'")
            }
        }
    }
}