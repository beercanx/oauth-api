package uk.co.baconi.oauth.api.common.client

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.encodeToJsonElement
import org.junit.jupiter.api.Test

class ClientTypeTest {

    private val json = Json

    private val ClientType.jsonValue: String
        get() = (json.encodeToJsonElement(this) as JsonPrimitive).content

    @Test
    fun `should throw exception on invalid type`() {
        shouldThrow<IllegalStateException> { ClientType.fromValue("aardvark") }
    }

    @Test
    fun `should be able to find valid client type from value`() {
        for (clientType in enumValues<ClientType>()) {
            ClientType.fromValue(clientType.value) shouldBe clientType
        }
    }

    @Test
    fun `json value should match from value method`() {
        for (clientType in enumValues<ClientType>()) {
            ClientType.fromValue(clientType.jsonValue) shouldBe clientType
        }
    }
}