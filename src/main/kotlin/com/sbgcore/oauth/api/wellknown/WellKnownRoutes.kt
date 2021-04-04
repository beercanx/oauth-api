package com.sbgcore.oauth.api.wellknown

import io.ktor.application.*
import io.ktor.response.*
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