package uk.co.baconi.oauth.api.common.client

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.util.*

class ClientTypeTest {

    @Test
    fun `should throw exception on invalid type`() {
        shouldThrow<NoSuchElementException> { ClientType.fromValue("aardvark") }
    }

    @Test
    fun `should be able to find valid client type from value`() {
        for (clientType in enumValues<ClientType>()) {
            ClientType.fromValue(clientType.value) shouldBe clientType
        }
    }
}