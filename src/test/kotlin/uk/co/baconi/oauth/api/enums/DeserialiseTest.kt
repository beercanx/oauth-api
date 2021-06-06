package uk.co.baconi.oauth.api.enums

import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.api.scopes.Scopes

class DeserialiseTest {

    @Test
    fun `should return enum for string representation`() {
        "openid".deserialise<Scopes>() shouldBe Scopes.OpenId
    }

    @Test
    fun `should return null for non existent enum`() {
        "mock-scope".deserialise<Scopes>() should beNull()
    }
}