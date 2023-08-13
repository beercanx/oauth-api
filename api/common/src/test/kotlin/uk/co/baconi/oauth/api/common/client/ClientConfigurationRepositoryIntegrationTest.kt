package uk.co.baconi.oauth.api.common.client

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.beEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.sequences.shouldContain
import io.kotest.matchers.sequences.shouldHaveSize
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.api.common.grant.GrantType
import uk.co.baconi.oauth.api.common.scope.Scope

class ClientConfigurationRepositoryIntegrationTest {

    private val underTest = ClientConfigurationRepository()

    @Nested
    inner class ToUrls {

        @Test
        fun `should be able to handle missing entry`() {
            assertSoftly(underTest.findById(ClientId("redirect-uris-missing"))) {
                shouldNotBeNull()
                redirectUris should beEmpty()
            }
        }

        @Test
        fun `should be able to handle null entry`() {
            assertSoftly(underTest.findById(ClientId("redirect-uris-null"))) {
                shouldNotBeNull()
                redirectUris should beEmpty()
            }
        }

        @Test
        fun `should be able to handle empty entry`() {
            assertSoftly(underTest.findById(ClientId("redirect-uris-empty"))) {
                shouldNotBeNull()
                redirectUris should beEmpty()
            }
        }

        @Test
        fun `should be able to handle valid uri entry`() {
            assertSoftly(underTest.findById(ClientId("redirect-uris-valid"))) {
                shouldNotBeNull()
                redirectUris shouldContain "uk.co.baconi.valid://callback"
            }
        }
    }

    @Nested
    inner class ToScopes {

        @Test
        fun `should be able to handle missing entry`() {
            assertSoftly(underTest.findById(ClientId("allowed-scopes-missing"))) {
                shouldNotBeNull()
                allowedScopes should beEmpty()
            }
        }

        @Test
        fun `should be able to handle null entry`() {
            assertSoftly(underTest.findById(ClientId("allowed-scopes-null"))) {
                shouldNotBeNull()
                allowedScopes should beEmpty()
            }
        }

        @Test
        fun `should be able to handle empty entry`() {
            assertSoftly(underTest.findById(ClientId("allowed-scopes-empty"))) {
                shouldNotBeNull()
                allowedScopes should beEmpty()
            }
        }

        @Test
        fun `should be able to handle valid scope entry`() {
            assertSoftly(underTest.findById(ClientId("allowed-scopes-valid"))) {
                shouldNotBeNull()
                allowedScopes shouldContainExactly setOf(Scope.OpenId)
            }
        }
    }

    @Nested
    inner class FindById {

        @Test
        fun `should return configuration if its defined in config`() {
            underTest.findById(ClientId("consumer-y")) shouldBe ClientConfiguration(
                id = ClientId("consumer-y"),
                type = ClientType.Public,
                redirectUris = setOf("uk.co.baconi.consumer-y://callback"),
                allowedScopes = setOf(Scope.OpenId),
                allowedActions = setOf(ClientAction.ProofKeyForCodeExchange),
                allowedGrantTypes = setOf(GrantType.AuthorisationCode),
            )
        }

        @Test
        fun `should return null if the configuration does not exist`() {
            underTest.findById(ClientId("aardvark")).shouldBeNull()
        }
    }

    @Nested
    inner class FindByClientIdAsString {

        @Test
        fun `should call findById`() {
            underTest.findByClientId("consumer-y").shouldNotBeNull()
        }

        @Test
        fun `should return null if client does not exist`() {
            underTest.findByClientId("aardvark").shouldBeNull()
        }
    }

    @Nested
    inner class FindAllClientIds {

        @Test
        fun `should return all the known client ids`() {
            assertSoftly(underTest.findAllClientIds()) {
                shouldHaveSize(12) // 8 from application.conf and 4 from the reference.conf
                shouldContain(ClientId("no-op"))
                shouldContain(ClientId("consumer-x"))
                shouldContain(ClientId("consumer-y"))
                shouldContain(ClientId("consumer-z"))
            }
        }
    }
}