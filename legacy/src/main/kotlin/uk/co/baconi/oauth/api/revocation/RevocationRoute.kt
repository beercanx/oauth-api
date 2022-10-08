package uk.co.baconi.oauth.api.revocation

import io.ktor.server.resources.*
import io.ktor.server.routing.Route

interface RevocationRoute {

    // TODO - https://datatracker.ietf.org/doc/html/rfc7009
    fun Route.revocation() {
        post<RevocationLocation> { location ->
            TODO("Implement $location")
        }
    }
}
