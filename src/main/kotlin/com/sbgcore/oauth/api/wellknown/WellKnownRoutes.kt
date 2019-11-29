package com.sbgcore.oauth.api.wellknown

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.get

import io.ktor.routing.route
import io.ktor.routing.routing

fun Application.wellKnownRoutes(wellKnown: WellKnown = WellKnown()) {
    routing {
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
}