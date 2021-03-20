package com.sbgcore.oauth.api.wellknown

import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.*

fun Route.wellKnownRoutes(wellKnown: WellKnown = WellKnown()) {
    route("/.well-known") {
        get("/openid-configuration") {
            call.respond(wellKnown.getOpenIdConfiguration())
        }
        get("/jwks.json") {
            call.respond(wellKnown.getJsonWebKeySet())
        }
        get("/product-configuration") {
            call.respond(wellKnown.getProductConfiguration())
        }
    }
}