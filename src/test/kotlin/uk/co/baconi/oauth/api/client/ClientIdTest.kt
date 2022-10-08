package uk.co.baconi.oauth.api.client

import uk.co.baconi.oauth.api.client.ClientId.ConsumerX
import uk.co.baconi.oauth.api.client.ClientId.ConsumerZ
import uk.co.baconi.oauth.api.enums.serialise
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class ClientIdTest {

    @Test
    fun `ConsumerX client id should have a value of consumer-x`() {
        serialise(ConsumerX) shouldBe "consumer-x"
    }

    @Test
    fun `ConsumerZ client id should have a value of consumer-z`() {
        serialise(ConsumerZ) shouldBe "consumer-z"
    }
}