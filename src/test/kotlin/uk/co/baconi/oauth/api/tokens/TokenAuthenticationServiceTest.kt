package uk.co.baconi.oauth.api.tokens

import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class TokenAuthenticationServiceTest {

    private val repository = mockk<AccessTokenRepository>()
    private val underTest = TokenAuthenticationService(repository)

    @Nested
    inner class AccessTokenTest {

        @Test
        fun `should return null if there is no matching access token in the repository`() {

            every { repository.findByValue(any<String>()) } returns null

            underTest.accessToken("aardvark") should beNull()
        }

        @Test
        fun `should return null if discovered the access token has expired`() {

            every { repository.findByValue(any<String>()) } returns mockk {
                every { hasExpired() } returns true
            }

            underTest.accessToken("aardvark") should beNull()
        }

        @Test
        fun `should return the access token if its discovered and has not expired`() {

            val accessToken = mockk<AccessToken>("aardvark") {
                every { hasExpired() } returns false
            }

            every { repository.findByValue(any<String>()) } returns accessToken

            underTest.accessToken("aardvark") shouldBe accessToken
        }
    }
}