package uk.co.baconi.oauth.api.common.scope

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test

class ScopeSerializerTest {

    private val json = Json { encodeDefaults = true }

    @Test
    fun `should serialize a scope`() {
        json.encodeToString(ScopeSerializer, Scope("aardvark")) shouldBe """"aardvark""""
    }

    @Test
    fun `should refuse to deserialize a scope`() {
        shouldThrow<SerializationException> {
            json.decodeFromString(ScopeSerializer, """"aardvark"""")
        } shouldHaveMessage "Deserialize unsupported for Scope"
    }

}