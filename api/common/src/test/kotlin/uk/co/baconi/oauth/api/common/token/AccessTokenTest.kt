package uk.co.baconi.oauth.api.common.token

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.api.common.authentication.AuthenticatedUsername
import uk.co.baconi.oauth.api.common.client.ClientId
import java.time.OffsetDateTime.now
import java.util.UUID.randomUUID

class AccessTokenTest {

    @Nested
    inner class HasExpired {

        @Test
        fun `should return true if expires at is in the past`() {
            withClue("hasExpired") {
                AccessToken(
                    value = randomUUID(),
                    username = AuthenticatedUsername("aardvark"),
                    clientId = ClientId("consumer-z"),
                    scopes = emptySet(),
                    issuedAt = now(),
                    expiresAt = now().minusDays(1),
                    notBefore = now()
                ).hasExpired() shouldBe true
            }
        }

        @Test
        fun `should return false if expires at is in the future`() {
            withClue("hasExpired") {
                AccessToken(
                    value = randomUUID(),
                    username = AuthenticatedUsername("aardvark"),
                    clientId = ClientId("consumer-z"),
                    scopes = emptySet(),
                    issuedAt = now(),
                    expiresAt = now().plusDays(1),
                    notBefore = now()
                ).hasExpired() shouldBe false
            }
        }
    }

    @Nested
    inner class ToString {

        @Test
        fun `should not include access token value`() {
            val value = randomUUID()
            assertSoftly(
                AccessToken(
                    value = value,
                    username = AuthenticatedUsername("aardvark"),
                    clientId = ClientId("consumer-z"),
                    scopes = emptySet(),
                    issuedAt = now(),
                    expiresAt = now().minusDays(1),
                    notBefore = now()
                ).toString()
            ) {
                shouldNotContain(value.toString())
                shouldContain("value=REDACTED")
            }
        }
    }

    @Nested
    inner class EqualsAndHashCode {

        private val accessToken = AccessToken(
            value = randomUUID(),
            username = AuthenticatedUsername("aardvark"),
            clientId = ClientId("consumer-z"),
            scopes = emptySet(),
            issuedAt = now(),
            expiresAt = now().minusDays(1),
            notBefore = now()
        )

        @Test
        fun `should be equal if the access token values are identical`() {
            withClue("equalTo") {
                accessToken shouldBe accessToken.copy(username = AuthenticatedUsername("badger"))
            }
        }

        @Test
        fun `should not be equal if the access token values are not identical`() {
            withClue("equalTo") {
                accessToken shouldNotBe accessToken.copy(value = randomUUID())
            }
        }

        @Test
        fun `should have the same hash code if the access token values are identical`() {
            withClue("hashCode") {
                accessToken.hashCode() shouldBe accessToken.copy(username = AuthenticatedUsername("badger")).hashCode()
            }
        }

        @Test
        fun `should not have the same hash code if the access token values are not identical`() {
            withClue("hashCode") {
                accessToken.hashCode() shouldNotBe accessToken.copy(value = randomUUID()).hashCode()
            }
        }
    }
}