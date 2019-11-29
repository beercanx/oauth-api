package com.sbgcore.oauth.api.wellknown

import com.sbgcore.oauth.api.jwk.JsonWebKeySet
import io.kotlintest.assertSoftly
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.serialization
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import io.mockk.every
import io.mockk.mockk

class WellKnownRoutesSpec : StringSpec({

    val mockWellKnown = mockk<WellKnown>()
    val underTest: Application.() -> Unit = {

        // The routes we are testing
        wellKnownRoutes(mockWellKnown)

        // Add support for returning data classes
        install(ContentNegotiation) {
            serialization()
        }
    }

    "/.well-known/openid-configuration should return OpenID configuration" {

        every { mockWellKnown.getOpenIdConfiguration() } returns OpenIdConfiguration(
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

    "/.well-known/jwks.json should return some JWKS" {

        every { mockWellKnown.getJsonWebKeySet() } returns JsonWebKeySet(
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

    "/.well-known/openid-product should return some product configuration" {

        every { mockWellKnown.getProductConfiguration() } returns ProductConfiguration(emptyList())

        withTestApplication(underTest) {
            handleRequest(HttpMethod.Get, "/.well-known/product-configuration").apply {
                assertSoftly {
                    response.status() shouldBe HttpStatusCode.OK
                    response.content shouldBe """{"products":[]}"""
                }
            }
        }
    }
})