package uk.co.baconi.oauth.api.common.client

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ClientSecretServiceTest {

    private val consumerZConfiguration = mockk<ClientConfiguration> {
        every { id } returns ClientId("consumer-z")
        every { type } returns ClientType.Confidential
        every { isConfidential } returns true
        every { isPublic } returns false
    }

    private val consumerZSecret = mockk<ClientSecret> {
        every { clientId } returns ClientId("consumer-z")
        every { hashedSecret } returns "consumer-z-secret"
    }

    private val consumerYConfiguration = mockk<ClientConfiguration> {
        every { id } returns ClientId("consumer-y")
        every { type } returns ClientType.Public
        every { isConfidential } returns false
        every { isPublic } returns true
        every { allowedActions } returns emptySet()
    }

    private val consumerYSecret = mockk<ClientSecret> {
        every { clientId } returns ClientId("consumer-y")
        every { hashedSecret } returns "consumer-y-secret"
    }

    private val clientSecretRepository = mockk<ClientSecretRepository> {
        every { findAllByClientId(any<String>()) } returns emptySequence()
    }

    private val clientConfigurationRepository = mockk<ClientConfigurationRepository> {
        every { findById(any()) } returns null
        every { findByClientId(any()) } returns null
    }

    private val checkPassword = mockk<(String, CharArray) -> Boolean> {
        every { this@mockk.invoke(any(), any()) } returns false
    }

    private val underTest = ClientSecretService(
        clientSecretRepository,
        clientConfigurationRepository,
        checkPassword
    )

    @Nested
    inner class ConfidentialClientTest {

        @Test
        fun `return a ConfidentialClient when secret matches and its a confidential client`() {

            every { clientSecretRepository.findAllByClientId(any<String>()) } returns sequenceOf(consumerZSecret)
            every { checkPassword.invoke(any(), any()) } returns true
            every { clientConfigurationRepository.findById(any()) } returns consumerZConfiguration

            val client = underTest.authenticate("consumer-z", "consumer-z-secret")
            client.shouldNotBeNull()
            assertSoftly(client) {
                id shouldBe ClientId("consumer-z")
                configuration.id shouldBe ClientId("consumer-z")
                configuration.type shouldBe ClientType.Confidential
            }
        }

        @Test
        fun `return null when client secret does not exist`() {

            underTest.authenticate("consumer-z", "consumer-z-secret").shouldBeNull()
        }

        @Test
        fun `return null when client secret does not match`() {

            every { clientSecretRepository.findAllByClientId(any<ClientId>()) } returns sequenceOf(consumerZSecret)
            every { checkPassword.invoke(any(), any()) } returns false

            underTest.authenticate("consumer-z", "consumer-y-secret").shouldBeNull()
        }

        @Test
        fun `return null when client configuration does not exist`() {

            every { clientSecretRepository.findAllByClientId(any<ClientId>()) } returns sequenceOf(consumerZSecret)
            every { checkPassword.invoke(any(), any()) } returns true

            underTest.authenticate("consumer-z", "consumer-z-secret").shouldBeNull()
        }

        @Test
        fun `return null when client is not confidential`() {

            every { clientSecretRepository.findAllByClientId(any<String>()) } returns sequenceOf(consumerYSecret)
            every { checkPassword.invoke(any(), any()) } returns true
            every { clientConfigurationRepository.findById(any()) } returns consumerYConfiguration

            underTest.authenticate("consumer-y", "consumer-y-secret").shouldBeNull()
        }
    }

    @Nested
    inner class PublicClientTest {

        @Test
        fun `return a PublicClient when its a public client`() {

            every { clientConfigurationRepository.findByClientId(any()) } returns consumerYConfiguration

            val client = underTest.authenticate("consumer-y")
            client.shouldNotBeNull()

            assertSoftly(client) {
                id shouldBe ClientId("consumer-y")
                configuration.id shouldBe ClientId("consumer-y")
                configuration.type shouldBe ClientType.Public
            }
        }

        @Test
        fun `return null when client configuration does not exist`() {

            underTest.authenticate("consumer-x").shouldBeNull()
        }

        @Test
        fun `return null when client is not public`() {

            every { clientConfigurationRepository.findByClientId(any<String>()) } returns consumerZConfiguration

            underTest.authenticate("consumer-z").shouldBeNull()
        }

        @Test
        fun `return null when client id is null`() {

            underTest.authenticate(null).shouldBeNull()
        }
    }
}