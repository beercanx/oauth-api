package uk.co.baconi.oauth.api.common.client

import io.kotest.assertions.throwables.shouldThrowWithMessage
import io.kotest.matchers.shouldBe
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ClientIdSerializerTest {

    private val underTest = ClientIdSerializer

    private val json = Json { encodeDefaults = true }
    private fun encode(clientId: ClientId): String = json.encodeToString(underTest, clientId)
    private fun decode(data: String): ClientId = json.decodeFromString(underTest, data)

    @Nested
    inner class Encode {

        @Test
        fun `should fail to encode a blank client id`() {
            shouldThrowWithMessage<SerializationException>("Invalid client id [   ]") {
                encode(ClientId("   "))
            }
        }

        @Test
        fun `should fail to encode a client id with trailing whitespace`() {
            listOf("consumer-y ", " consumer-y", " consumer-y ").forEach { clientId ->
                shouldThrowWithMessage<SerializationException>("Invalid client id [$clientId]") {
                    encode(ClientId(clientId))
                }
            }
        }

        @Test
        fun `should be able to encode a client id`() {
            encode(ClientId("consumer-y")) shouldBe "\"consumer-y\""
        }
    }

    @Nested
    inner class Decode {

        @Test
        fun `should fail to decode a null json value`() {
            shouldThrowWithMessage<SerializationException>("Deserialize unsupported for ClientId") {
                decode("null")
            }
        }

        @Test
        fun `should fail to decode a blank json value`() {
            shouldThrowWithMessage<SerializationException>("Deserialize unsupported for ClientId") {
                decode("\"    \"")
            }
        }

        @Test
        fun `should fail to decode an empty json value`() {
            shouldThrowWithMessage<SerializationException>("Deserialize unsupported for ClientId") {
                decode("\"\"")
            }
        }

        @Test
        fun `should fail to decode an invalid client id`() {
            shouldThrowWithMessage<SerializationException>("Deserialize unsupported for ClientId") {
                decode("\"aardvark\"")
            }
        }

        @Test
        fun `should fail to decode a valid client id`() {
            shouldThrowWithMessage<SerializationException>("Deserialize unsupported for ClientId") {
                decode("\"consumer-x\"")
            }
        }
    }
}