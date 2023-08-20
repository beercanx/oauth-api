package uk.co.baconi.oauth.api.token

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.api.common.authentication.AuthenticatedUsername
import uk.co.baconi.oauth.api.common.authorisation.AuthorisationCode
import uk.co.baconi.oauth.api.common.client.ClientId
import uk.co.baconi.oauth.api.common.client.ConfidentialClient
import uk.co.baconi.oauth.api.common.scope.Scope

import uk.co.baconi.oauth.api.common.token.AccessTokenService
import uk.co.baconi.oauth.api.common.token.RefreshTokenService
import java.time.Instant
import java.util.*

class AuthorisationCodeGrantTest {

    private val accessToken = UUID.randomUUID()
    private val accessTokenService = mockk<AccessTokenService> {
        every { issue(any(), any(), any()) } returns mockk {
            val now = Instant.now()
            every { value } returns accessToken
            every { scopes } returns setOf(Scope("basic"))
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

    private val underTest = AuthorisationCodeGrant(accessTokenService, refreshTokenService)

    @Test
    fun `should return success when customer authentication succeeds`() {

        val principal = mockk<ConfidentialClient> {
            every { id } returns ClientId("badger")
        }

        val authorisationCode = mockk<AuthorisationCode> {
            every { username } returns AuthenticatedUsername("aardvark")
            every { clientId } returns principal.id
            every { scopes } returns setOf(Scope("basic"))
            every { state } returns "a5385765-0a4b-41be-bb97-5415a5c2be67"
        }

        assertSoftly(underTest.exchange(AuthorisationCodeRequest(principal, authorisationCode))) {
            shouldBeInstanceOf<TokenResponse.Success>()
            this.accessToken shouldBe accessToken
            this.refreshToken shouldBe refreshToken
            this.expiresIn shouldBe 0
            this.scope shouldContainExactly setOf(Scope("basic"))
            this.state shouldBe "a5385765-0a4b-41be-bb97-5415a5c2be67"
        }
    }
}