package uk.co.baconi.oauth.api.common.authentication

import io.kotest.assertions.throwables.shouldThrowWithMessage
import io.kotest.matchers.shouldBe
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.common.authentication.AuthenticatedUsername
import uk.co.baconi.oauth.common.authentication.AuthenticatedUsernameSerializer

class AuthenticatedUsernameSerializerTest {

    private val underTest = AuthenticatedUsernameSerializer

    private val json = Json { encodeDefaults = true }
    private fun encode(username: AuthenticatedUsername): String = json.encodeToString(underTest, username)
    private fun decode(data: String): AuthenticatedUsername = json.decodeFromString(underTest, data)

    @Nested
    inner class Encode {

        @Test
        fun `should fail to encode a blank authenticated username`() {
            shouldThrowWithMessage<SerializationException>("Invalid authenticated username [   ]") {
                encode(AuthenticatedUsername("   "))
            }
        }

        @Test
        fun `should fail to encode a authenticated username with trailing whitespace`() {
            listOf("aardvark ", " aardvark", " aardvark ").forEach { clientId ->
                shouldThrowWithMessage<SerializationException>("Invalid authenticated username [$clientId]") {
                    encode(AuthenticatedUsername(clientId))
                }
            }
        }

        @Test
        fun `should be able to encode a authenticated username`() {
            encode(AuthenticatedUsername("aardvark")) shouldBe "\"aardvark\""
        }
    }

    @Nested
    inner class Decode {

        @Test
        fun `should fail to decode a null json value`() {
            shouldThrowWithMessage<SerializationException>("Deserialize unsupported for AuthenticatedUsername in Kotlin/JVM") {
                decode("null")
            }
        }

        @Test
        fun `should fail to decode a blank json value`() {
            shouldThrowWithMessage<SerializationException>("Deserialize unsupported for AuthenticatedUsername in Kotlin/JVM") {
                decode("\"    \"")
            }
        }

        @Test
        fun `should fail to decode an empty json value`() {
            shouldThrowWithMessage<SerializationException>("Deserialize unsupported for AuthenticatedUsername in Kotlin/JVM") {
                decode("\"\"")
            }
        }

        @Test
        fun `should fail to decode an invalid authenticated username`() {
            shouldThrowWithMessage<SerializationException>("Deserialize unsupported for AuthenticatedUsername in Kotlin/JVM") {
                decode("\"aardvark\"")
            }
        }

        @Test
        fun `should fail to decode a valid authenticated username`() {
            shouldThrowWithMessage<SerializationException>("Deserialize unsupported for AuthenticatedUsername in Kotlin/JVM") {
                decode("\"consumer-x\"")
            }
        }
    }
}