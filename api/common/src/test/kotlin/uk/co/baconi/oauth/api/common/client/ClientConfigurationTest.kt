package uk.co.baconi.oauth.api.common.client

import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.api.common.client.ClientType.Confidential
import uk.co.baconi.oauth.api.common.client.ClientType.Public

class ClientConfigurationTest {

    private val consumerX = ClientConfiguration(
        id = ClientId("consumer-x"),
        type = Confidential,
        redirectUris = emptySet(),
        allowedScopes = emptySet(),
        allowedActions = emptySet(),
        allowedGrantTypes = emptySet(),
    )

    private val consumerY = ClientConfiguration(
        id = ClientId("consumer-y"),
        type = Public,
        redirectUris = emptySet(),
        allowedScopes = emptySet(),
        allowedActions = emptySet(),
        allowedGrantTypes = emptySet(),
    )

    @Nested
    inner class IsConfidential {

        @Test
        fun `should return true when client type is Confidential`() {
            consumerX.isConfidential shouldBe true
        }

        @Test
        fun `should return false when client type is Public`() {
            consumerY.isConfidential shouldBe false
        }
    }

    @Nested
    inner class IsPublic {

        @Test
        fun `should return true when client type is Public`() {
            consumerY.isPublic shouldBe true
        }

        @Test
        fun `should return false when client type is Confidential`() {
            consumerX.isPublic shouldBe false
        }
    }

    @Nested
    inner class EqualsAndHashCode {

        private val config = ClientConfiguration(
            id = ClientId("aardvark"),
            type = Confidential,
            redirectUris = emptySet(),
            allowedScopes = emptySet(),
            allowedActions = emptySet(),
            allowedGrantTypes = emptySet(),
        )

        @Test
        fun `should be equal if the client configuration ids are identical`() {
            withClue("equalTo") {
                config shouldBe config.copy(type = Public)
            }
        }

        @Test
        fun `should not be equal if the client configuration ids are not identical`() {
            withClue("equalTo") {
                config shouldNotBe config.copy(id = ClientId("badger"))
            }
        }

        @Test
        fun `should have the same hash code if the client configuration ids are identical`() {
            withClue("hashCode") {
                config.hashCode() shouldBe config.copy(type = Public).hashCode()
            }
        }

        @Test
        fun `should not have the same hash code if the client configuration ids are not identical`() {
            withClue("hashCode") {
                config.hashCode() shouldNotBe config.copy(id = ClientId("badger")).hashCode()
            }
        }
    }
}
