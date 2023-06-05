package uk.co.baconi.oauth.api.common.authorisation

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.api.common.authentication.AuthenticatedUsername
import uk.co.baconi.oauth.api.common.client.ClientId
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

class AuthorisationCodeTest {

    @Nested
    inner class Basic {

        @Nested
        inner class HasExpired {

            @Test
            fun `should return true if expires at is in the past`() {
                withClue("hasExpired") {
                    AuthorisationCode.Basic(
                        value = UUID.randomUUID(),
                        username = AuthenticatedUsername("aardvark"),
                        clientId = ClientId("consumer-z"),
                        scopes = emptySet(),
                        redirectUri = "uk.co.baconi.oauth.consumerz://callback",
                        issuedAt = Instant.now(),
                        expiresAt = Instant.now().minus(1, ChronoUnit.DAYS),
                        state = null,
                    ).hasExpired() shouldBe true
                }
            }

            @Test
            fun `should return false if expires at is in the future`() {
                withClue("hasExpired") {
                    AuthorisationCode.Basic(
                        value = UUID.randomUUID(),
                        username = AuthenticatedUsername("aardvark"),
                        clientId = ClientId("consumer-z"),
                        scopes = emptySet(),
                        redirectUri = "uk.co.baconi.oauth.consumerz://callback",
                        issuedAt = Instant.now(),
                        expiresAt = Instant.now().plus(1, ChronoUnit.DAYS),
                        state = null,
                    ).hasExpired() shouldBe false
                }
            }
        }

        @Nested
        inner class ToString {

            @Test
            fun `should not include authorisation code value`() {
                val value = UUID.randomUUID()
                assertSoftly(
                    AuthorisationCode.Basic(
                        value = value,
                        username = AuthenticatedUsername("aardvark"),
                        clientId = ClientId("consumer-z"),
                        scopes = emptySet(),
                        redirectUri = "uk.co.baconi.oauth.consumerz://callback",
                        issuedAt = Instant.now(),
                        expiresAt = Instant.now().minus(1, ChronoUnit.DAYS),
                        state = null,
                    ).toString()
                ) {
                    shouldNotContain(value.toString())
                    shouldContain("value='REDACTED'")
                }
            }
        }
    }

    @Nested
    inner class PKCE {

        @Nested
        inner class HasExpired {

            @Test
            fun `should return true if expires at is in the past`() {
                withClue("hasExpired") {
                    AuthorisationCode.PKCE(
                        value = UUID.randomUUID(),
                        username = AuthenticatedUsername("aardvark"),
                        clientId = ClientId("consumer-z"),
                        scopes = emptySet(),
                        redirectUri = "uk.co.baconi.oauth.consumerz://callback",
                        issuedAt = Instant.now(),
                        expiresAt = Instant.now().minus(1, ChronoUnit.DAYS),
                        codeChallenge = CodeChallenge(""),
                        codeChallengeMethod = CodeChallengeMethod.S256,
                        state = null,
                    ).hasExpired() shouldBe true
                }
            }

            @Test
            fun `should return false if expires at is in the future`() {
                withClue("hasExpired") {
                    AuthorisationCode.PKCE(
                        value = UUID.randomUUID(),
                        username = AuthenticatedUsername("aardvark"),
                        clientId = ClientId("consumer-z"),
                        scopes = emptySet(),
                        redirectUri = "uk.co.baconi.oauth.consumerz://callback",
                        issuedAt = Instant.now(),
                        expiresAt = Instant.now().plus(1, ChronoUnit.DAYS),
                        codeChallenge = CodeChallenge(""),
                        codeChallengeMethod = CodeChallengeMethod.S256,
                        state = null,
                    ).hasExpired() shouldBe false
                }
            }
        }

        @Nested
        inner class ToString {

            @Test
            fun `should not include authorisation code value`() {
                val value = UUID.randomUUID()
                assertSoftly(
                    AuthorisationCode.PKCE(
                        value = value,
                        username = AuthenticatedUsername("aardvark"),
                        clientId = ClientId("consumer-z"),
                        scopes = emptySet(),
                        redirectUri = "uk.co.baconi.oauth.consumerz://callback",
                        issuedAt = Instant.now(),
                        expiresAt = Instant.now().minus(1, ChronoUnit.DAYS),
                        codeChallenge = CodeChallenge(""),
                        codeChallengeMethod = CodeChallengeMethod.S256,
                        state = null,
                    ).toString()
                ) {
                    shouldNotContain(value.toString())
                    shouldContain("value='REDACTED'")
                }
            }
        }
    }
}