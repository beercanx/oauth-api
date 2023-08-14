package uk.co.baconi.oauth.api.common.token

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.api.common.authentication.AuthenticatedUsername
import uk.co.baconi.oauth.api.common.client.ClientId
import java.time.Instant.now
import java.time.temporal.ChronoUnit
import java.util.UUID.randomUUID

class RefreshTokenTest {

    @Nested
    inner class HasExpired {

        @Test
        fun `should return true if expires at is in the past`() {
            withClue("hasExpired") {
                RefreshToken(
                    value = randomUUID(),
                    username = AuthenticatedUsername("aardvark"),
                    clientId = ClientId("consumer-z"),
                    scopes = emptySet(),
                    issuedAt = now(),
                    expiresAt = now().minus(1, ChronoUnit.DAYS),
                    notBefore = now()
                ).hasExpired() shouldBe true
            }
        }

        @Test
        fun `should return false if expires at is in the future`() {
            withClue("hasExpired") {
                RefreshToken(
                    value = randomUUID(),
                    username = AuthenticatedUsername("aardvark"),
                    clientId = ClientId("consumer-z"),
                    scopes = emptySet(),
                    issuedAt = now(),
                    expiresAt = now().plus(1, ChronoUnit.DAYS),
                    notBefore = now()
                ).hasExpired() shouldBe false
            }
        }
    }

    @Nested
    inner class IsBefore {

        @Test
        fun `should return false if not before is in the past`() {
            withClue("isBefore") {
                RefreshToken(
                    value = randomUUID(),
                    username = AuthenticatedUsername("aardvark"),
                    clientId = ClientId("consumer-z"),
                    scopes = emptySet(),
                    issuedAt = now(),
                    expiresAt = now(),
                    notBefore = now().minus(1, ChronoUnit.DAYS)
                ).isBefore() shouldBe false
            }
        }

        @Test
        fun `should return true if not before is in the future`() {
            withClue("isBefore") {
                RefreshToken(
                    value = randomUUID(),
                    username = AuthenticatedUsername("aardvark"),
                    clientId = ClientId("consumer-z"),
                    scopes = emptySet(),
                    issuedAt = now(),
                    expiresAt = now(),
                    notBefore = now().plus(1, ChronoUnit.DAYS)
                ).isBefore() shouldBe true
            }
        }
    }

    @Nested
    inner class ToString {

        @Test
        fun `should not include refresh token value`() {
            val value = randomUUID()
            assertSoftly(
                RefreshToken(
                    value = value,
                    username = AuthenticatedUsername("aardvark"),
                    clientId = ClientId("consumer-z"),
                    scopes = emptySet(),
                    issuedAt = now(),
                    expiresAt = now().minus(1, ChronoUnit.DAYS),
                    notBefore = now()
                ).toString()
            ) {
                shouldNotContain(value.toString())
                shouldContain("value='REDACTED'")
            }
        }
    }
}