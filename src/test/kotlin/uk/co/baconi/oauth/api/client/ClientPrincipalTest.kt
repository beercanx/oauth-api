package uk.co.baconi.oauth.api.client

import uk.co.baconi.oauth.api.client.ClientId.ConsumerY
import uk.co.baconi.oauth.api.client.ClientId.ConsumerZ
import uk.co.baconi.oauth.api.client.ClientType.Confidential
import uk.co.baconi.oauth.api.client.ClientType.Public
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ClientPrincipalTest {

    @Nested
    inner class ConfidentialClientTest {

        @Test
        fun `should construct for a confidential client`() {

            val client = ConfidentialClient(mockk {
                every { id } returns ConsumerZ
                every { type } returns Confidential
            })

            assertSoftly(client) {
                id shouldBe ConsumerZ
                configuration.type shouldBe Confidential
            }
        }

        @Test
        fun `throw exception if the configuration is not for a confidential client`() {

            val exception = shouldThrow<IllegalArgumentException> {
                ConfidentialClient(mockk {
                    every { id } returns ConsumerY
                    every { type } returns Public
                })
            }

            exception shouldHaveMessage "type cannot be [Public]"
        }
    }

    @Nested
    inner class PublicClientTest {

        @Test
        fun `should construct for a public client`() {

            val client = PublicClient(mockk {
                every { id } returns ConsumerY
                every { type } returns Public
            })

            assertSoftly(client) {
                id shouldBe ConsumerY
                configuration.type shouldBe Public
            }
        }

        @Test
        fun `throw exception if the configuration is not for a public client`() {

            val exception = shouldThrow<IllegalArgumentException> {
                PublicClient(mockk {
                    every { id } returns ConsumerZ
                    every { type } returns Confidential
                })
            }

            exception shouldHaveMessage "type cannot be [Confidential]"
        }
    }
}