package uk.co.baconi.oauth.api.common.grant

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.encodeToJsonElement
import org.junit.jupiter.api.Test

class GrantTypeTest {

    private val json = Json

    private val GrantType.jsonValue: String
        get() = (json.encodeToJsonElement(this) as JsonPrimitive).content

    @Test
    fun `should throw exception on invalid grant type from value`() {
        shouldThrow<IllegalStateException> { GrantType.fromValue("aardvark") }
    }

    @Test
    fun `should return null on invalid grant type from value or null`() {
        GrantType.fromValueOrNull("aardvark").shouldBeNull()
    }

    @Test
    fun `should be able to find valid grant type from value`() {
        for (grantType in enumValues<GrantType>()) {
            GrantType.fromValue(grantType.value) shouldBe grantType
        }
    }

    @Test
    fun `json value should match from value method`() {
        for (grantType in enumValues<GrantType>()) {
            GrantType.fromValue(grantType.jsonValue) shouldBe grantType
        }
    }
}