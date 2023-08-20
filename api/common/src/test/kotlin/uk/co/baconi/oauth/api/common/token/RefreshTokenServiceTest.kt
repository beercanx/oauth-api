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
import uk.co.baconi.oauth.api.common.client.ClientPrincipal
import uk.co.baconi.oauth.api.common.scope.Scope

import java.time.LocalDateTime.ofInstant
import java.time.ZoneOffset
import java.util.*

class RefreshTokenServiceTest {

    private val refreshTokenSlot = slot<RefreshToken>()
    private val repository = mockk<RefreshTokenRepository> {
        every { insert(capture(refreshTokenSlot)) } returns Unit
    }

    private val underTest = RefreshTokenService(repository)
    
    private val clientPrincipal = mockk<ClientPrincipal>()

    companion object {
        private val ConsumerZ = ClientId("consumer-z")
    }

    @Nested
    inner class Issue {

        @Test
        fun `should insert the issued refresh token into the repository`() {

            val result = underTest.issue(
                username = AuthenticatedUsername("aardvark"),
                clientId = ConsumerZ,
                scopes = setOf(Scope("basic"))
            )

            refreshTokenSlot.captured shouldBe result
        }

        @Test
        fun `should issue the refresh token with sensible date values`() {

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
    inner class Verify {

        @Test
        fun `should return null if String token is an invalid format`() {
            underTest.verify(clientPrincipal, "aardvark") should beNull()
        }

        @Test
        fun `should return null if there is no matching refresh token in the repository`() {

            every { repository.findById(any()) } returns null

            underTest.verify(clientPrincipal, UUID.randomUUID()) should beNull()
        }

        @Test
        fun `should return null if the client principal does not match the client on the refresh token`() {

            every { repository.findById(any()) } returns mockk {
                every { hasExpired() } returns false
                every { isBefore() } returns false
                every { clientId } returns ClientId("badger")
            }

            every { clientPrincipal.id } returns ClientId("aardvark")

            underTest.verify(clientPrincipal, UUID.randomUUID()) should beNull()

        }

        @Test
        fun `should return null if discovered the refresh token has expired`() {

            every { repository.findById(any()) } returns mockk {
                every { hasExpired() } returns true
                every { isBefore() } returns false
                every { clientId } returns ClientId("aardvark")
            }

            every { clientPrincipal.id } returns ClientId("aardvark")

            every { repository.deleteByRecord(any()) } returns Unit

            underTest.verify(clientPrincipal, UUID.randomUUID()) should beNull()

            verify { repository.deleteByRecord(any()) }
        }

        @Test
        fun `should return null if discovered the refresh token is yet to be valid`() {

            every { repository.findById(any()) } returns mockk {
                every { hasExpired() } returns false
                every { isBefore() } returns true
                every { clientId } returns ClientId("aardvark")
            }

            every { clientPrincipal.id } returns ClientId("aardvark")

            underTest.verify(clientPrincipal, UUID.randomUUID()) should beNull()
        }

        @Test
        fun `should return the refresh token if its discovered and has not expired`() {

            val refreshToken = mockk<RefreshToken>("aardvark") {
                every { hasExpired() } returns false
                every { isBefore() } returns false
                every { clientId } returns ClientId("aardvark")
            }

            every { clientPrincipal.id } returns ClientId("aardvark")

            every { repository.findById(any()) } returns refreshToken

            underTest.verify(clientPrincipal, UUID.randomUUID()) shouldBe refreshToken
        }

        @Test
        fun `should return the refresh token if by String its discovered and has not expired`() {

            val refreshToken = mockk<RefreshToken> {
                every { value } returns UUID.fromString("536023bf-8673-441d-a81a-ffe1b03cf698")
                every { hasExpired() } returns false
                every { isBefore() } returns false
                every { clientId } returns ClientId("aardvark")
            }

            every { clientPrincipal.id } returns ClientId("aardvark")

            every { repository.findById(any()) } returns refreshToken

            underTest.verify(clientPrincipal, "536023bf-8673-441d-a81a-ffe1b03cf698") shouldBe refreshToken
        }
    }
}