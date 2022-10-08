package uk.co.baconi.oauth.api.common.scope

import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.api.common.claim.Claim.Subject
import uk.co.baconi.oauth.api.common.scope.Scope.OpenId
import uk.co.baconi.oauth.api.common.scope.Scope.ProfileWrite

class ScopeConfigurationRepositoryIntegrationTest {

    private val underTest = ScopeConfigurationRepository()

    @Test
    fun `should return scope configuration for a scope with configuration`() {
        underTest.findById(OpenId) shouldBe ScopeConfiguration(OpenId, setOf(Subject))
    }

    @Test
    fun `should return null for a scope without configuration`() {
        underTest.findById(ProfileWrite).shouldBeNull()
    }
}