package uk.co.baconi.oauth.api.client

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.api.client.ClientId.ConsumerX
import uk.co.baconi.oauth.api.client.ClientId.ConsumerY
import uk.co.baconi.oauth.api.client.ClientType.Confidential
import uk.co.baconi.oauth.api.client.ClientType.Public

class ClientConfigurationTest {

    private val consumerX = ClientConfiguration(
        id = ConsumerX,
        type = Confidential,
        redirectUris = emptySet(),
        allowedScopes = emptySet(),
        allowedResponseTypes = emptySet(),
    )

    private val consumerY = ClientConfiguration(
        id = ConsumerY,
        type = Public,
        redirectUris = emptySet(),
        allowedScopes = emptySet(),
        allowedResponseTypes = emptySet(),
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
}
