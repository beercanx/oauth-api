package uk.co.baconi.oauth.api.common.scope

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.encodeToJsonElement
import org.junit.jupiter.api.Test

class ScopeTest {

    private val json = Json

    private val Scope.jsonValue: String
        get() = (json.encodeToJsonElement(this) as JsonPrimitive).content

    @Test
    fun `should throw exception on invalid scope from value`() {
        shouldThrow<IllegalStateException> { Scope.fromValue("aardvark") }
    }

    @Test
    fun `should return null on invalid scope from value or null`() {
        Scope.fromValueOrNull("aardvark").shouldBeNull()
    }

    @Test
    fun `should be able to find valid scope from value`() {
        for (scope in enumValues<Scope>()) {
            Scope.fromValue(scope.value) shouldBe scope
        }
    }

    @Test
    fun `json value should match from value method`() {
        for (scope in enumValues<Scope>()) {
            Scope.fromValue(scope.jsonValue) shouldBe scope
        }
    }
}