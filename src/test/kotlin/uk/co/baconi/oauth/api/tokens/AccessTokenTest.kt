package uk.co.baconi.oauth.api.tokens

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.api.client.ClientId
import uk.co.baconi.oauth.api.client.ClientId.ConsumerZ
import java.time.OffsetDateTime
import java.time.OffsetDateTime.now
import java.util.*
import java.util.UUID.randomUUID

class AccessTokenTest {

    @Nested
    inner class HasExpired {

        @Test
        fun `should return true if expires at is in the past`() {
            withClue("hasExpired") {
                AccessToken(
                    id = randomUUID(),
                    value = randomUUID(),
                    username = "aardvark",
                    clientId = ConsumerZ,
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
                    id = randomUUID(),
                    value = randomUUID(),
                    username = "aardvark",
                    clientId = ConsumerZ,
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
        fun `should not include access toke value`() {
            val value = randomUUID()
            assertSoftly(AccessToken(
                id = randomUUID(),
                value = value,
                username = "aardvark",
                clientId = ConsumerZ,
                scopes = emptySet(),
                issuedAt = now(),
                expiresAt = now().minusDays(1),
                notBefore = now()
            ).toString()) {
                shouldNotContain(value.toString())
                shouldContain("value=REDACTED")
            }
        }
    }
}