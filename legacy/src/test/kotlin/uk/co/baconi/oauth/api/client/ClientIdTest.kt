package uk.co.baconi.oauth.api.client

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.api.client.ClientId.ConsumerX
import uk.co.baconi.oauth.api.client.ClientId.ConsumerZ
import uk.co.baconi.oauth.api.enums.serialise

class ClientIdTest {

    @Test
    fun `ConsumerX client id should have a value of consumer-x`() {
        ConsumerX.serialise() shouldBe "consumer-x"
    }

    @Test
    fun `ConsumerZ client id should have a value of consumer-z`() {
        ConsumerZ.serialise() shouldBe "consumer-z"
    }
}