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
import uk.co.baconi.oauth.api.common.authorisation.AuthorisationCode
import uk.co.baconi.oauth.api.common.client.ClientId
import uk.co.baconi.oauth.api.common.client.ConfidentialClient
import uk.co.baconi.oauth.api.common.scope.Scope.OpenId
import uk.co.baconi.oauth.api.common.scope.Scope.ProfileRead
import uk.co.baconi.oauth.api.common.token.AccessTokenService
import uk.co.baconi.oauth.api.common.token.RefreshToken
import uk.co.baconi.oauth.api.common.token.RefreshTokenService
import java.time.Instant
import java.util.*

class RefreshTokenGrantTest {

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

    private val underTest = RefreshTokenGrant(accessTokenService, refreshTokenService)

    @Test
    fun `should return success when customer authentication succeeds`() {

        val principal = mockk<ConfidentialClient> {
            every { id } returns ClientId("badger")
        }

        val oldRefreshToken = mockk<RefreshToken> {
            every { username } returns AuthenticatedUsername("aardvark")
            every { clientId } returns principal.id
            every { scopes } returns setOf(OpenId, ProfileRead)
        }

        assertSoftly(underTest.exchange(RefreshTokenRequest(principal, setOf(OpenId), oldRefreshToken))) {
            shouldBeInstanceOf<TokenResponse.Success>()
            this.accessToken shouldBe accessToken
            this.refreshToken shouldBe refreshToken
            this.expiresIn shouldBe 0
            this.scope shouldContainExactly setOf(OpenId)
            this.state should beNull()
        }
    }
}