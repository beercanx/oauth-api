package uk.co.baconi.oauth.api.common.client

import io.kotest.matchers.string.shouldNotContain
import org.junit.jupiter.api.Test
import java.util.*

class ClientSecretTest {

    @Test
    fun `should not include secret in toString() output`() {
        ClientSecret(UUID.randomUUID(), ClientId("ConsumerZ"), "password").toString() shouldNotContain "password"
    }

}