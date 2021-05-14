package uk.co.baconi.oauth.api.enums

import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.api.scopes.Scopes

class SerialiseTest {

    @Test
    fun `should return string representation for a real enum`() {
        serialise(Scopes.OpenId) shouldBe "openid"
    }

    @Test
    fun `should return null for fake enum`() {
        serialise(mockk<Scopes>("mock-scope")) should beNull()
    }
}