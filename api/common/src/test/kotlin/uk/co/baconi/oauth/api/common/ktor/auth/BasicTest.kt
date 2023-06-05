package uk.co.baconi.oauth.api.common.ktor.auth

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.ktor.server.auth.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.api.common.client.ConfidentialClient

class BasicTest {

    @Test
    fun `should register the authentication with name based on class`() {

        val config = mockk<AuthenticationConfig>()

        val slot = slot<AuthenticationProvider>()
        every { config.register(capture(slot)) } returns Unit

        config.basic<ConfidentialClient> {}

        assertSoftly(slot.captured) {
            shouldBeInstanceOf<BasicAuthenticationProvider>()
            name shouldBe "uk.co.baconi.oauth.api.common.client.ConfidentialClient"
        }
    }
}