package uk.co.baconi.oauth.api.wellknown

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

interface WellKnownRoutes {

    val wellKnown: WellKnown
        get() = WellKnown()

    fun Route.wellKnownRoutes() {
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
