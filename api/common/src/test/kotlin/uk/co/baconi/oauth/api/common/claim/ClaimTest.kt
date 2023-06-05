package uk.co.baconi.oauth.api.common.claim

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.encodeToJsonElement
import org.junit.jupiter.api.Test

class ClaimTest {

    private val json = Json

    private val Claim.jsonValue: String
        get() = (json.encodeToJsonElement(this) as JsonPrimitive).content

    @Test
    fun `should throw exception on invalid claim from value`() {
        shouldThrow<IllegalStateException> { Claim.fromValue("aardvark") }
    }

    @Test
    fun `should return null on invalid claim from value or null`() {
        Claim.fromValueOrNull("aardvark").shouldBeNull()
    }

    @Test
    fun `should be able to find valid claim from value`() {
        for (claim in enumValues<Claim>()) {
            Claim.fromValue(claim.value) shouldBe claim
        }
    }

    @Test
    fun `json value should match from value method`() {
        for (claim in enumValues<Claim>()) {
            Claim.fromValue(claim.jsonValue) shouldBe claim
        }
    }
}