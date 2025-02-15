package uk.co.baconi.oauth.api.common.scope

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.beEmpty
import io.kotest.matchers.collections.containExactly
import io.kotest.matchers.should
import io.kotest.matchers.throwable.shouldHaveMessage
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test

class ScopesDeserializerTest {

    private val json = Json { encodeDefaults = true }
    private fun encode(scope: Set<String>): String = json.encodeToString(ScopesDeserializer, scope)
    private fun decode(data: String): Set<String> = json.decodeFromString(ScopesDeserializer, data)

    @Test
    fun `should fail to encode with the deserializer`() {
        shouldThrow<SerializationException> {
            encode(setOf(""))
        } shouldHaveMessage "Encoding not supported"
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