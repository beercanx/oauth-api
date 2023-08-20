package uk.co.baconi.oauth.api.common.scope

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.beEmpty
import io.kotest.matchers.collections.containExactly
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test

class ScopesSerializerTest {

    private val json = Json { encodeDefaults = true }
    private fun encode(scope: Set<Scope>): String = json.encodeToString(ScopesSerializer, scope)

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
    fun `should fail to decode with the serializer`() {
        shouldThrow<SerializationException> {
            json.decodeFromString(ScopesSerializer, "\"\"")
        } shouldHaveMessage "Decoding not supported"
    }
}