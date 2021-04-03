package com.sbgcore.oauth.api.client

import com.sbgcore.oauth.api.client.ClientId.ConsumerX
import com.sbgcore.oauth.api.client.ClientId.ConsumerY
import com.sbgcore.oauth.api.client.ClientType.Confidential
import com.sbgcore.oauth.api.client.ClientType.Public
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ClientConfigurationTest {

    private val consumerX = ClientConfiguration(
        id = ConsumerX,
        type = Confidential,
        redirectUrls = emptySet()
    )

    private val consumerY = ClientConfiguration(
        id = ConsumerY,
        type = Public,
        redirectUrls = emptySet()
    )

    @Nested
    inner class IsConfidential {

        @Test
        fun `should return true when client type is Confidential`() {
            consumerX.isConfidential shouldBe true
        }

        @Test
        fun `should return false when client type is Public`() {
            consumerY.isConfidential shouldBe false
        }
    }

    @Nested
    inner class IsPublic {

        @Test
        fun `should return true when client type is Public`() {
            consumerY.isPublic shouldBe true
        }

        @Test
        fun `should return false when client type is Confidential`() {
            consumerX.isPublic shouldBe false
        }
    }
}
