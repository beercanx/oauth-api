package uk.co.baconi.oauth.api.common.scope

import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.api.common.claim.Claim

class ScopeConfigurationTest {

    @Nested
    inner class EqualsAndHashCode {

        private val config = ScopeConfiguration(
            id = Scope.OpenId,
            claims = setOf(Claim.Subject),
        )

        @Test
        fun `should be equal if the scope configuration ids are identical`() {
            withClue("equalTo") {
                config shouldBe config.copy(claims = emptySet())
            }
        }

        @Test
        fun `should not be equal if the scope configuration ids are not identical`() {
            withClue("equalTo") {
                config shouldNotBe config.copy(id = Scope.ProfileRead)
            }
        }

        @Test
        fun `should have the same hash code if the scope configuration ids are identical`() {
            withClue("hashCode") {
                config.hashCode() shouldBe config.copy(claims = emptySet()).hashCode()
            }
        }

        @Test
        fun `should not have the same hash code if the scope configuration ids are not identical`() {
            withClue("hashCode") {
                config.hashCode() shouldNotBe config.copy(id = Scope.ProfileRead).hashCode()
            }
        }
    }
}