package uk.co.baconi.oauth.api.common.scope

import io.kotest.matchers.collections.beEmpty
import io.kotest.matchers.collections.containExactly
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test

class ScopesSerialisationTests {

    private val json = Json { encodeDefaults = true }
    private fun encode(scope: Set<Scope>): String = json.encodeToString(ScopesSerializer, scope)
    private fun decode(data: String): Set<String> = json.decodeFromString(ScopesDeserializer, data)

    @Test
    fun `should be able to encode an empty set of scopes`() {
        encode(emptySet()) shouldBe "\"\""
    }

    @Test
    fun `should be able to encode a singleton set of scopes`() {
        encode(setOf(Scope("basic"))) shouldBe "\"basic\""
    }

    @Test
    fun `should be able to encode a full set of scopes`() {
        encode(
            setOf(
                Scope("basic"),
                Scope("profile::read"),
                Scope("profile::write")
            )
        ) shouldBe "\"basic profile::read profile::write\""
    }

    @Test
    fun `should be able to decode an empty set of scopes`() {
        decode("\"\"") should beEmpty()
    }

    @Test
    fun `should be able to decode a singleton set of scopes`() {
        decode("\"basic\"") should containExactly("basic")
    }

    @Test
    fun `should be able to decode a full set of scopes`() {
        decode("\"basic profile::read profile::write\"") should containExactly(
            "basic",
            "profile::read",
            "profile::write"
        )
    }
}