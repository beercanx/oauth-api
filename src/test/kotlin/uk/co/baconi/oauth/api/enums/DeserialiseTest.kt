package uk.co.baconi.oauth.api.enums

import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.api.openid.Scopes

class DeserialiseTest {

    @Test
    fun `should return enum for string representation`() {
        deserialise<Scopes>("openid") shouldBe Scopes.OpenId
    }

    @Test
    fun `should return null for non existent enum`() {
        deserialise<Scopes>("mock-scope") should beNull()
    }
}