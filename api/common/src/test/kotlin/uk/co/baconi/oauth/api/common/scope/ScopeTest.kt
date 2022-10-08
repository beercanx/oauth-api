package uk.co.baconi.oauth.api.common.scope

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class ScopeTest {

    @Test
    fun `should throw exception on invalid type from value`() {
        shouldThrow<NoSuchElementException> { Scope.fromValue("aardvark") }
    }

    @Test
    fun `should return null on invalid type from value or null`() {
        Scope.fromValueOrNull("aardvark") shouldBe null
    }

    @Test
    fun `should be able to find valid client type from value`() {
        for (scope in enumValues<Scope>()) {
            Scope.fromValue(scope.value) shouldBe scope
        }
    }
}