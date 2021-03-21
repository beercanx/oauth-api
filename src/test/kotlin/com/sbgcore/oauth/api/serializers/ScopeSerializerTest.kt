package com.sbgcore.oauth.api.serializers

import com.sbgcore.oauth.api.openid.Scopes
import com.sbgcore.oauth.api.openid.Scopes.*
import io.kotest.matchers.collections.beEmpty
import io.kotest.matchers.collections.containExactly
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test

class ScopeSerializerTest {

    private val underTest = ScopeSerializer(mockk())

    private val json = Json { encodeDefaults = true }
    private fun encode(scope: Set<Scopes>): String = json.encodeToString(underTest, scope)
    private fun decode(data: String): Set<Scopes> = json.decodeFromString(underTest, data)

    @Test
    fun `should be able to encode an empty set of scopes`() {
        encode(emptySet()) shouldBe "\"\""
    }

    @Test
    fun `should be able to encode a singleton set of scopes`() {
        encode(setOf(OpenId)) shouldBe "\"openid\""
    }

    @Test
    fun `should be able to encode a full set of scopes`() {
        encode(setOf(OpenId, ProfileRead, ProfileWrite)) shouldBe "\"openid profile::read profile::write\""
    }

    @Test
    fun `should be able to decode an empty set of scopes`() {
        decode("\"\"") should beEmpty()
    }

    @Test
    fun `should be able to decode a singleton set of scopes`() {
        decode("\"openid\"") should containExactly(OpenId)
    }

    @Test
    fun `should be able to decode a full set of scopes`() {
        decode("\"openid profile::read profile::write\"") should containExactly(OpenId, ProfileRead, ProfileWrite)
    }

    @Test
    fun `should be able to handle decoding an aardvark`() {
        decode("\"aardvark\"") should beEmpty()
    }
}