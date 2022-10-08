package uk.co.baconi.oauth.api.common.customer

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
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

    @Nested
    inner class EqualsAndHashCode {

        private val aardvark = CustomerCredential(
            username = "aardvark",
            hashedSecret = "hashed-secret",
        )

        private val badger = aardvark.copy(
            username = "badger"
        )

        @Test
        fun `should be equal if the customer credential usernames are identical`() {
            withClue("equalTo") {
                aardvark shouldBe aardvark.copy(hashedSecret = "another-hashed-secret")
            }
        }

        @Test
        fun `should not be equal if the customer credential usernames are not identical`() {
            withClue("equalTo") {
                aardvark shouldNotBe badger
            }
        }

        @Test
        fun `should have the same hash code if the customer credential usernames are identical`() {
            withClue("hashCode") {
                aardvark.hashCode() shouldBe aardvark.copy(hashedSecret = "another-hashed-secret").hashCode()
            }
        }

        @Test
        fun `should not have the same hash code if the customer credential usernames are not identical`() {
            withClue("hashCode") {
                aardvark.hashCode() shouldNotBe badger.hashCode()
            }
        }
    }
}