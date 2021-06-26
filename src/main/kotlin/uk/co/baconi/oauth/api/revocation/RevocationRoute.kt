package uk.co.baconi.oauth.api.revocation

import io.ktor.locations.post
import io.ktor.routing.Route

interface RevocationRoute {

    // TODO - https://datatracker.ietf.org/doc/html/rfc7009
    fun Route.revocation() {
        post<RevocationLocation> { location ->
            TODO("Implement $location")
        }
    }
}
