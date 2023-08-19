package uk.co.baconi.oauth.api.user.info

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import uk.co.baconi.oauth.api.common.authentication.AuthenticatedUsername
import uk.co.baconi.oauth.api.common.claim.Claim
import uk.co.baconi.oauth.api.common.claim.Claim.Subject
import uk.co.baconi.oauth.api.common.claim.Claim.UpdatedAt
import uk.co.baconi.oauth.api.common.scope.ScopeConfigurationRepository

class UserInfoServiceTest {

    private val scopeConfigurationRepository = mockk<ScopeConfigurationRepository>()

    private val underTest = UserInfoService(scopeConfigurationRepository)

    @Test
    fun `should return no data when no scopes have claims`() {

        every { scopeConfigurationRepository.findById(any()) } returns mockk {
            every { claims } returns emptySet()
        }

        assertSoftly(underTest.getUserInfo(accessToken())) { userInfo ->
            userInfo.subject should beNull()
            userInfo.updatedAt should beNull()
        }
    }

    @ParameterizedTest
    @EnumSource(value = Claim::class)
    fun `should return claim when a scope has it defined`(claim: Claim) {

        every { scopeConfigurationRepository.findById(any()) } returns mockk {
            every { claims } returns setOf(claim)
        }

        val result = underTest.getUserInfo(accessToken())

        val value: Any? = when(claim) {
            Subject -> result.subject
            UpdatedAt -> result.updatedAt
        }

        value shouldNot beNull()
    }

    @Test
    fun `subject should be the username in the access token`() {

        every { scopeConfigurationRepository.findById(any()) } returns mockk {
            every { claims } returns setOf(Subject)
        }

        val result = underTest.getUserInfo(accessToken(username = "cicada"))

        result.subject shouldBe AuthenticatedUsername("cicada")
    }
}