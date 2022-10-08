package uk.co.baconi.oauth.api.wellknown

import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*

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
