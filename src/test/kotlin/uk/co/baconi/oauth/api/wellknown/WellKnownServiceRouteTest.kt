package uk.co.baconi.oauth.api.wellknown

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.locations.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test

class WellKnownServiceRouteTest : WellKnownRoute {

    override val wellKnownService = mockk<WellKnownService>()
    private val underTest: Application.() -> Unit = {

        // Add support for returning data classes
        install(ContentNegotiation) {
            json()
        }

        // Add support for "typed" locations
        install(Locations)

        // The routes we are testing
        routing {
            wellKnown()
        }
    }

    @Test
    fun `well known openid configuration endpoint should return OpenID configuration`() {

        every { wellKnownService.getOpenIdConfiguration() } returns OpenIdConfiguration(
            "issuer",
            "endpoint"
        )

        withTestApplication(underTest) {
            handleRequest(HttpMethod.Get, "/.well-known/openid-configuration").apply {
                assertSoftly {
                    response.status() shouldBe HttpStatusCode.OK
                    response.content shouldBe """{"issuer":"issuer","authorization_endpoint":"endpoint"}"""
                }
            }
        }
    }

    @Test
    fun `well known jwks endpoint should return some JWKS`() {

        every { wellKnownService.getJsonWebKeySet() } returns JsonWebKeySet(
            emptySet()
        )

        withTestApplication(underTest) {
            handleRequest(HttpMethod.Get, "/.well-known/jwks.json").apply {
                assertSoftly {
                    response.status() shouldBe HttpStatusCode.OK
                    response.content shouldBe """{"keys":[]}"""
                }
            }
        }
    }

    @Test
    fun `well known openid product endpoint should return some product configuration`() {

        every { wellKnownService.getProductConfiguration() } returns ProductConfiguration(emptyList())

        withTestApplication(underTest) {
            handleRequest(HttpMethod.Get, "/.well-known/product-configuration").apply {
                assertSoftly {
                    response.status() shouldBe HttpStatusCode.OK
                    response.content shouldBe """{"products":[]}"""
                }
            }
        }
    }
}