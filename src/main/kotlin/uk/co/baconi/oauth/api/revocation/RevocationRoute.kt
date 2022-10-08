package uk.co.baconi.oauth.api.revocation

import io.ktor.locations.*
import io.ktor.routing.*

interface RevocationRoute {

    // TODO - https://datatracker.ietf.org/doc/html/rfc7009
    fun Route.revocation() {
        post<RevocationLocation> { location ->
            TODO("Implement $location")
        }
    }
}
