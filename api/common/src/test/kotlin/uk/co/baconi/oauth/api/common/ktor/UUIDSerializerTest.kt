package uk.co.baconi.oauth.api.common.ktor

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

class UUIDSerializerTest {

    private val underTest = UUIDSerializer
    private val json = Json

    private val uuidString = "c2a94941-3e1f-46aa-9c13-8dd9fe13720b"
    private val uuid = UUID.fromString(uuidString)

    @Nested
    inner class Serialize {

        @Test
        fun `should be able to serialise a UUID`() {
            json.encodeToString(underTest, uuid) shouldBe """"$uuidString""""
        }
    }

    @Nested
    inner class Deserialize {

        @Test
        fun `should be able to deserialise a UUID`() {
            json.decodeFromString(underTest, """"$uuidString"""") shouldBe uuid
        }

        @Test
        fun `should throw SerializationException when deserialising an invalid UUID`() {
            shouldThrow<SerializationException> {
                json.decodeFromString(underTest, """"aardvark"""")
            }
        }
    }

    @Nested
    inner class FromValueOrNull {

        @Test
        fun `should be able to deserialise a UUID`() {
            underTest.fromValueOrNull(uuidString) shouldBe uuid
        }

        @Test
        fun `should return null when null is passed in`() {
            underTest.fromValueOrNull(null) should beNull()
        }

        @Test
        fun `should return null when deserialising an invalid UUID`() {
            underTest.fromValueOrNull("aardvark") should beNull()
        }
    }
}