package com.sbgcore.oauth.api.openid

import com.sbgcore.oauth.api.openid.ClientId.ConsumerX
import com.sbgcore.oauth.api.openid.ClientId.ConsumerZ
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class ClientIdTest {

    @Test
    fun `ConsumerX client id should have a value of consumer-x`() {
        ConsumerX.value shouldBe "consumer-x"
    }

    @Test
    fun `ConsumerZ client id should have a value of consumer-z`() {
        ConsumerZ.value shouldBe "consumer-z"
    }
}