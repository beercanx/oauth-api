package uk.co.baconi.oauth.api.token

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.api.common.client.ClientPrincipal
import uk.co.baconi.oauth.api.common.token.RefreshToken
import java.util.UUID

class RefreshTokenRequestTest {

    @Nested
    inner class ToString {

        @Test
        fun `should not include refresh token value`() {
            val uuid = UUID.randomUUID()
            assertSoftly(
                RefreshTokenRequest(
                    principal = mockk<ClientPrincipal>(),
                    scopes = emptySet(),
                    refreshToken = mockk<RefreshToken> { every { value } returns uuid }
                ).toString()
            ) {
                shouldNotContain("$uuid")
                shouldContain("refreshToken='REDACTED'")
            }
        }
    }
}