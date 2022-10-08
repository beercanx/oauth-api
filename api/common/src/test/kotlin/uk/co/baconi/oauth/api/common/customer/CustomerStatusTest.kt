package uk.co.baconi.oauth.api.common.customer

import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CustomerStatusTest {

    @Nested
    inner class EqualsAndHashCode {

        private val aardvark = CustomerStatus(
            username = "aardvark",
            state = CustomerState.Active,
        )

        private val badger = aardvark.copy(
            username = "badger"
        )

        @Test
        fun `should be equal if the customer status usernames are identical`() {
            withClue("equalTo") {
                aardvark shouldBe aardvark.copy(state = CustomerState.Closed)
            }
        }

        @Test
        fun `should not be equal if the customer status usernames are not identical`() {
            withClue("equalTo") {
                aardvark shouldNotBe badger
            }
        }

        @Test
        fun `should have the same hash code if the customer status usernames are identical`() {
            withClue("hashCode") {
                aardvark.hashCode() shouldBe aardvark.copy(state = CustomerState.Suspended).hashCode()
            }
        }

        @Test
        fun `should not have the same hash code if the customer status usernames are not identical`() {
            withClue("hashCode") {
                aardvark.hashCode() shouldNotBe badger.hashCode()
            }
        }
    }
}