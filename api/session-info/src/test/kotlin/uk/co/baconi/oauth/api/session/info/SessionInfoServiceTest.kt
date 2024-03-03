package uk.co.baconi.oauth.api.session.info

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.api.common.authentication.AuthenticatedSession
import uk.co.baconi.oauth.api.common.authentication.AuthenticatedUsername
import uk.co.baconi.oauth.api.common.client.ClientId
import uk.co.baconi.oauth.api.common.token.AccessToken
import uk.co.baconi.oauth.api.common.token.AccessTokenRepository
import uk.co.baconi.oauth.api.common.token.RefreshToken
import uk.co.baconi.oauth.api.common.token.RefreshTokenRepository
import java.time.Instant

class SessionInfoServiceTest {

    private val accessTokenRepository = mockk<AccessTokenRepository>()
    private val refreshTokenRepository = mockk<RefreshTokenRepository>()

    private val underTest = SessionInfoService(accessTokenRepository, refreshTokenRepository)

    @Test
    fun shouldReturnNothingWhenNotAuthenticated() {

        assertSoftly(underTest.getSessionInfo(null)) {
            shouldBeInstanceOf<SessionInfoResponse>()
            session should beNull()
            tokens should beNull()
        }
    }

    @Test
    fun shouldReturnTokensByUsernameWhenAuthenticated() {

        val accessToken = mockk<AccessToken> {
            every { clientId } returns ClientId("badger")
            every { issuedAt } returns Instant.ofEpochSecond(1712495346)
            every { expiresAt } returns Instant.ofEpochSecond(1712495346)
        }
        val refreshToken = mockk<RefreshToken> {
            every { clientId } returns ClientId("badger")
            every { issuedAt } returns Instant.ofEpochSecond(1712495345)
            every { expiresAt } returns Instant.ofEpochSecond(1712495345)
        }

        val aardvark = AuthenticatedUsername("aardvark")
        val authenticated = AuthenticatedSession(aardvark)

        every { accessTokenRepository.findAllByUsername(aardvark) } returns listOf(accessToken)
        every { refreshTokenRepository.findAllByUsername(aardvark) } returns listOf(refreshToken)

        assertSoftly(underTest.getSessionInfo(authenticated)) {
            shouldBeInstanceOf<SessionInfoResponse>()
            val (session,tokens) = this
            session shouldBe authenticated
            tokens.shouldNotBeNull()
            tokens.accessTokens shouldHaveSize 1
            tokens.refreshTokens shouldHaveSize 1

            assertSoftly(tokens.accessTokens.first()) {
                clientId shouldBe ClientId("badger")
                issuedAt shouldBe Instant.ofEpochSecond(1712495346)
                expiresAt shouldBe Instant.ofEpochSecond(1712495346)
            }

            assertSoftly(tokens.refreshTokens.first()) {
                clientId shouldBe ClientId("badger")
                issuedAt shouldBe Instant.ofEpochSecond(1712495345)
                expiresAt shouldBe Instant.ofEpochSecond(1712495345)
            }
        }
    }
}