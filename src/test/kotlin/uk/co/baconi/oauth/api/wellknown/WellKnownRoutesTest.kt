package uk.co.baconi.oauth.api.wellknown

import uk.co.baconi.oauth.api.jwk.JsonWebKeySet
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.routing.*
import io.ktor.serialization.json
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test

class WellKnownRoutesTest : WellKnownRoutes {

    override val wellKnown = mockk<WellKnown>()
    private val underTest: Application.() -> Unit = {

        // The routes we are testing
        routing {
            wellKnownRoutes()
        }

        // Add support for returning data classes
        install(ContentNegotiation) {
            json()
        }
    }

    @Test
    fun `well known openid configuration endpoint should return OpenID configuration`() {

        every { wellKnown.getOpenIdConfiguration() } returns OpenIdConfiguration(
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

        every { wellKnown.getJsonWebKeySet() } returns JsonWebKeySet(
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

        every { wellKnown.getProductConfiguration() } returns ProductConfiguration(emptyList())

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