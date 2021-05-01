package uk.co.baconi.oauth.api.enums

import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.api.openid.Scopes

class EnumSerialisationTest {

    private val json = spyk(Json { })
    private val underTest = EnumSerialisation(json)

    @Nested
    inner class Serialise {

        @Test
        fun `should return string representation for a real enum`() {
            underTest.serialise(Scopes.serializer(), Scopes.OpenId) shouldBe "openid"
        }

        @Test
        fun `should return null for a non real enum`() {
            underTest.serialise(Scopes.serializer(), mockk("mock-scope")) should beNull()
        }

        @Test
        fun `should return null when serialisation exception is thrown by json library`() {

            every { json.encodeToJsonElement<Scopes>(any(), any()) } throws SerializationException("mock-exception")

            underTest.serialise(Scopes.serializer(), Scopes.OpenId) should beNull()
        }

        @Test
        fun `should return null for a null json element`() {
            every { json.encodeToJsonElement<Scopes>(any(), any()) } returns JsonNull

            underTest.serialise(Scopes.serializer(), Scopes.OpenId) should beNull()
        }

        @Test
        fun `should return null when json library returns unexpected json element`() {

            every { json.encodeToJsonElement<Scopes>(any(), any()) } returns JsonArray(emptyList())

            underTest.serialise(Scopes.serializer(), Scopes.OpenId) should beNull()
        }
    }

    @Nested
    inner class Deserialise {

        @Test
        fun `should return enum for a real string representation`() {
            underTest.deserialise(Scopes.serializer(), "openid") shouldBe Scopes.OpenId
        }

        @Test
        fun `should return null for a non existent enum`() {
            underTest.deserialise(Scopes.serializer(), "mock-scope") should beNull()
        }

        @Test
        fun `should return null when serialisation exception is thrown by json library`() {

            every { json.decodeFromJsonElement<Scopes>(any(), any()) } throws SerializationException("mock-exception")

            underTest.deserialise(Scopes.serializer(), "openid") should beNull()
        }
    }
}