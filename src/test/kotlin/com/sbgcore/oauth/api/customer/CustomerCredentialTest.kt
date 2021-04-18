package com.sbgcore.oauth.api.customer

import com.sbgcore.oauth.api.customer.CustomerCredential
import io.kotest.matchers.string.shouldNotContain
import org.junit.jupiter.api.Test

class CustomerCredentialTest {

    @Test
    fun `should not include secret in toString() output`() {
        CustomerCredential(username = "aardvark", secret = "badger").toString() shouldNotContain "badger"
    }
}