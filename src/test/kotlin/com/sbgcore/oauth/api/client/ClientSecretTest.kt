package com.sbgcore.oauth.api.client

import com.sbgcore.oauth.api.client.ClientId.ConsumerZ
import io.kotest.matchers.string.shouldNotContain
import org.junit.jupiter.api.Test
import java.util.*

class ClientSecretTest {

    @Test
    fun `should not include secret in toString() output`() {
        ClientSecret(UUID.randomUUID(), ConsumerZ, "password").toString() shouldNotContain "password"
    }

}