package uk.co.baconi.oauth.api.token

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.api.common.authentication.AuthenticatedUsername
import uk.co.baconi.oauth.api.common.authentication.CustomerAuthentication.Failure
import uk.co.baconi.oauth.api.common.authentication.CustomerAuthentication.Failure.Reason.Closed
import uk.co.baconi.oauth.api.common.authentication.CustomerAuthentication.Success
import uk.co.baconi.oauth.api.common.authentication.CustomerAuthenticationService
import uk.co.baconi.oauth.api.common.client.ClientId
import uk.co.baconi.oauth.api.common.client.ConfidentialClient
import uk.co.baconi.oauth.api.common.scope.Scope.OpenId
import uk.co.baconi.oauth.api.common.token.AccessTokenService
import uk.co.baconi.oauth.api.common.token.RefreshTokenService
import uk.co.baconi.oauth.api.token.TokenErrorType.InvalidGrant
import java.time.Instant
import java.util.*

class PasswordGrantTest {
    
    private val accessToken = UUID.randomUUID()
    private val accessTokenService = mockk<AccessTokenService> {
        every { issue(any(), any(), any()) } returns mockk {
            val now = Instant.now()
            every { value } returns accessToken
            every { scopes } returns setOf(OpenId)
            every { issuedAt } returns now
            every { expiresAt } returns now
        }
    }

    private val refreshToken = UUID.randomUUID()
    private val refreshTokenService = mockk<RefreshTokenService> {
        every { issue(any(), any(), any()) } returns mockk {
            every { value } returns refreshToken
        }
    }

    private val authenticationService = mockk<CustomerAuthenticationService>()

    private val underTest = PasswordGrant(accessTokenService, refreshTokenService, authenticationService)

    @Test
    fun `should return failure when customer authentication fails`() {

        every { authenticationService.authenticate(any(), any()) } returns Failure(Closed)

        val request = PasswordRequest(mockk(), setOf(OpenId), "aardvark", "1234".toCharArray())

        assertSoftly(underTest.exchange(request)) {
            shouldBeInstanceOf<TokenResponse.Failed>()
            error shouldBe InvalidGrant
            errorDescription shouldBe "Closed"
        }
    }

    @Test
    fun `should return success when customer authentication succeeds`() {

        every { authenticationService.authenticate(any(), any()) } returns Success(AuthenticatedUsername("aardvark"))

        val principal = mockk<ConfidentialClient> {
            every { id } returns ClientId("badger")
        }

        val request = PasswordRequest(principal, setOf(OpenId), "aardvark", "1234".toCharArray())

        assertSoftly(underTest.exchange(request)) {
            shouldBeInstanceOf<TokenResponse.Success>()
            this.accessToken shouldBe accessToken
            this.refreshToken shouldBe refreshToken
            this.expiresIn shouldBe 0
            this.scope shouldContainExactly setOf(OpenId)
            this.state should beNull()
        }
    }
}