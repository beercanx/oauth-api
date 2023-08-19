package uk.co.baconi.oauth.api.common.token

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.date.shouldBeAfter
import io.kotest.matchers.date.shouldBeBefore
import io.kotest.matchers.date.shouldBeToday
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.api.common.authentication.AuthenticatedUsername
import uk.co.baconi.oauth.api.common.client.ClientId
import uk.co.baconi.oauth.api.common.scope.Scope.OpenId
import java.time.LocalDateTime.ofInstant
import java.time.ZoneOffset
import java.util.*

class AccessTokenServiceTest {

    private val accessTokenSlot = slot<AccessToken>()
    private val repository = mockk<AccessTokenRepository> {
        every { insert(capture(accessTokenSlot)) } returns Unit
    }

    private val underTest = AccessTokenService(repository)

    companion object {
        private val ConsumerZ = ClientId("consumer-z")
    }

    @Nested
    inner class Issue {

        @Test
        fun `should insert the issued access token into the repository`() {

            val result = underTest.issue(
                username = AuthenticatedUsername("aardvark"),
                clientId = ConsumerZ,
                scopes = setOf(OpenId)
            )

            accessTokenSlot.captured shouldBe result
        }

        @Test
        fun `should issue the access token with sensible date values`() {

            val result = underTest.issue(
                username = AuthenticatedUsername("aardvark"),
                clientId = ConsumerZ,
                scopes = emptySet()
            )

            assertSoftly(result) {
                ofInstant(issuedAt, ZoneOffset.UTC).shouldBeToday()
                expiresAt shouldBeAfter issuedAt
                notBefore shouldBeBefore issuedAt
            }
        }
    }

    @Nested
    inner class Authenticate {

        @Test
        fun `should return null if String token is an invalid format`() {
            underTest.authenticate("aardvark") should beNull()
        }

        @Test
        fun `should return null if there is no matching access token in the repository`() {

            every { repository.findById(any()) } returns null

            underTest.authenticate(UUID.randomUUID()) should beNull()
        }

        @Test
        fun `should return null if discovered the access token has expired`() {

            every { repository.findById(any()) } returns mockk {
                every { hasExpired() } returns true
                every { isBefore() } returns false
            }

            every { repository.deleteByRecord(any()) } returns Unit

            underTest.authenticate(UUID.randomUUID()) should beNull()

            verify { repository.deleteByRecord(any()) }
        }

        @Test
        fun `should return null if discovered the access token is yet to be valid`() {

            every { repository.findById(any()) } returns mockk {
                every { hasExpired() } returns false
                every { isBefore() } returns true
            }

            underTest.authenticate(UUID.randomUUID()) should beNull()
        }

        @Test
        fun `should return the access token if its discovered and has not expired`() {

            val accessToken = mockk<AccessToken>("aardvark") {
                every { hasExpired() } returns false
                every { isBefore() } returns false
            }

            every { repository.findById(any()) } returns accessToken

            underTest.authenticate(UUID.randomUUID()) shouldBe accessToken
        }

        @Test
        fun `should return the access token if by String its discovered and has not expired`() {

            val accessToken = mockk<AccessToken> {
                every { value } returns UUID.fromString("536023bf-8673-441d-a81a-ffe1b03cf698")
                every { hasExpired() } returns false
                every { isBefore() } returns false
            }

            every { repository.findById(any()) } returns accessToken

            underTest.authenticate("536023bf-8673-441d-a81a-ffe1b03cf698") shouldBe accessToken
        }
    }
}