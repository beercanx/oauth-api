package uk.co.baconi.oauth.api.client

import io.kotest.matchers.string.shouldNotContain
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.api.client.ClientId.ConsumerZ
import java.util.*

class ClientSecretTest {

    @Test
    fun `should not include secret in toString() output`() {
        ClientSecret(UUID.randomUUID(), ConsumerZ, "password").toString() shouldNotContain "password"
    }

}