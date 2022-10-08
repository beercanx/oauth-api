package uk.co.baconi.oauth.api.wellknown

import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*

@Location("/.well-known")
object WellKnown {

    // TODO https://developer.okta.com/docs/reference/api/oidc/#well-known-oauth-authorization-server

    @Location("/openid-configuration")
    data class OpenIdConfiguration(val wellKnown: WellKnown) {
        constructor() : this(WellKnown)
    }

    @Location("/jwks.json")
    data class JsonWebKeySetConfiguration(val wellKnown: WellKnown) {
        constructor() : this(WellKnown)
    }

    @Location("/product-configuration")
    data class ProductConfiguration(val wellKnown: WellKnown) {
        constructor() : this(WellKnown)
    }
}

interface WellKnownRoutes {

    val wellKnownService: WellKnownService
        get() = WellKnownService()

    fun Route.wellKnownRoutes() {
        get<WellKnown.OpenIdConfiguration> {
            call.respond(wellKnownService.getOpenIdConfiguration())
        }
        get<WellKnown.JsonWebKeySetConfiguration> {
            call.respond(wellKnownService.getJsonWebKeySet())
        }
        get<WellKnown.ProductConfiguration> {
            call.respond(wellKnownService.getProductConfiguration())
        }
    }
}
