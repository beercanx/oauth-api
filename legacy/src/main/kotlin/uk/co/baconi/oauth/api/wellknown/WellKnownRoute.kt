package uk.co.baconi.oauth.api.wellknown

import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.Route

interface WellKnownRoute {

    val wellKnownService: WellKnownService
        get() = WellKnownService()

    fun Route.wellKnown() {

        // TODO https://developer.okta.com/docs/reference/api/oidc/#well-known-oauth-authorization-server

        get<OpenIdConfigurationLocation> {
            call.respond(wellKnownService.getOpenIdConfiguration())
        }

        get<JsonWebKeySetLocation> {
            call.respond(wellKnownService.getJsonWebKeySet())
        }

        get<ProductConfigurationLocation> {
            call.respond(wellKnownService.getProductConfiguration())
        }
    }
}
