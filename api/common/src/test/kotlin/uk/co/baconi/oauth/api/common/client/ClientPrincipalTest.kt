package uk.co.baconi.oauth.api.common.client

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import io.kotest.matchers.throwable.shouldHaveMessage
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.api.common.client.ClientAction.Authorise
import uk.co.baconi.oauth.api.common.client.ClientAction.ProofKeyForCodeExchange
import uk.co.baconi.oauth.api.common.client.ClientType.Confidential
import uk.co.baconi.oauth.api.common.client.ClientType.Public
import uk.co.baconi.oauth.api.common.grant.GrantType
import uk.co.baconi.oauth.api.common.grant.GrantType.*
import uk.co.baconi.oauth.api.common.scope.Scope

class ClientPrincipalTest {

    companion object {
        private val ConsumerZ = ClientId("consumer-z")
        private val ConsumerY = ClientId("consumer-y")
    }

    @Nested
    inner class CommonTest {

        private val underTest = ConfidentialClient(mockk {
            every { id } returns ConsumerZ
            every { type } returns Confidential
            every { allowedActions } returns emptySet()
            every { allowedScopes } returns setOf(Scope.OpenId)
            every { redirectUris } returns setOf("https://example.com")
        })

        @Test
        fun `should return true if the scope can be issued to the principal`() {
            withClue("canBeIssued(Scope.OpenId)") {
                underTest.canBeIssued(Scope.OpenId) shouldBe true
            }
        }

        @Test
        fun `should return false if the scope cannot be issued to the principal`() {
            withClue("canBeIssued(Scope.ProfileRead)") {
                underTest.canBeIssued(Scope.ProfileRead) shouldBe false
            }
        }

        @Test
        fun `should return true if the redirect ui is registered with the principal`() {
            withClue("hasRedirectUri(https://example.com)") {
                underTest.hasRedirectUri("https://example.com") shouldBe true
            }
        }

        @Test
        fun `should return false if the redirect ui is not registered with the principal`() {
            withClue("hasRedirectUri(https://example.co.uk)") {
                underTest.hasRedirectUri("https://example.co.uk") shouldBe false
            }
        }
    }

    @Nested
    inner class ConfidentialClientTest {

        @Test
        fun `should construct for a confidential client`() {

            val underTest = ConfidentialClient(mockk {
                every { id } returns ConsumerZ
                every { type } returns Confidential
                every { allowedActions } returns emptySet()
            })

            assertSoftly(underTest) {
                id shouldBe ConsumerZ
                configuration.type shouldBe Confidential
            }
        }

        @Test
        fun `should throw exception if the configuration is not for a confidential client`() {

            val exception = shouldThrow<IllegalArgumentException> {
                ConfidentialClient(mockk {
                    every { id } returns ConsumerY
                    every { type } returns Public
                })
            }

            exception shouldHaveMessage "type cannot be [Public]"
        }

        @Test
        fun `should not include client configuration`() {
            assertSoftly(
                ConfidentialClient(mockk {
                    every { id } returns ConsumerZ
                    every { type } returns Confidential
                    every { allowedActions } returns emptySet()
                }).toString()
            ) {
                shouldNotContain("allowedActions")
                shouldContain("configuration=REDACTED")
            }
        }
    }

    @Nested
    inner class PublicClientTest {

        @Test
        fun `should construct for a public client`() {

            val underTest = PublicClient(mockk {
                every { id } returns ConsumerY
                every { type } returns Public
                every { allowedActions } returns emptySet()
                every { allowedGrantTypes } returns emptySet()
            })

            assertSoftly(underTest) {
                id shouldBe ConsumerY
                configuration.type shouldBe Public
            }
        }

        @Test
        fun `should throw an exception if the configuration is not for a public client`() {

            val exception = shouldThrow<IllegalArgumentException> {
                PublicClient(mockk {
                    every { id } returns ConsumerZ
                    every { type } returns Confidential
                })
            }

            exception shouldHaveMessage "type cannot be [Confidential]"
        }

        @Test
        fun `should allow a public client to perform pkce based authentication code grants`() {

            val underTest = PublicClient(mockk {
                every { id } returns ConsumerY
                every { type } returns Public
                every { allowedActions } returns setOf(ProofKeyForCodeExchange)
                every { allowedGrantTypes } returns setOf(AuthorisationCode)
            })

            assertSoftly(underTest) {
                can(ProofKeyForCodeExchange) shouldBe true
                enumValues<GrantType>().filterNot(AuthorisationCode::equals).forEach { grantType ->
                    withClue("grantType: $grantType") {
                        can(grantType) shouldBe false
                    }
                }
            }
        }

        @Test
        fun `should throw an exception if a public client is configured to perform banned actions`() {

            val expectedBanned = enumValues<ClientAction>()
                .filterNot(Authorise::equals)
                .filterNot(ProofKeyForCodeExchange::equals)

            assertSoftly {
                expectedBanned.forEach { action ->
                    withClue("expected banned action: $action") {
                        shouldThrow<IllegalArgumentException> {
                            PublicClient(mockk {
                                every { id } returns ConsumerY
                                every { type } returns Public
                                every { allowedActions } returns setOf(action)
                                every { allowedGrantTypes } returns emptySet()
                            })
                        }
                    }
                }
            }
        }

        @Test
        fun `should throw an exception if a public client is configured to perform banned grant types`() {

            val expectedBanned = enumValues<GrantType>()
                .filterNot(RefreshToken::equals)
                .filterNot(Assertion::equals)

            assertSoftly {
                expectedBanned.forEach { grantType ->
                    withClue("expected banned grantType: $grantType") {
                        shouldThrow<IllegalArgumentException> {
                            PublicClient(mockk {
                                every { id } returns ConsumerY
                                every { type } returns Public
                                every { allowedActions } returns emptySet()
                                every { allowedGrantTypes } returns setOf(grantType)
                            })
                        }
                    }
                }
            }
        }

        @Test
        fun `should not include client configuration`() {
            assertSoftly(
                PublicClient(mockk {
                    every { id } returns ConsumerY
                    every { type } returns Public
                    every { allowedActions } returns emptySet()
                    every { allowedGrantTypes } returns emptySet()
                }).toString()
            ) {
                shouldNotContain("allowedActions")
                shouldContain("configuration=REDACTED")
            }
        }
    }
}