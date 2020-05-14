package com.sbgcore.oauth.api

import com.sbgcore.oauth.api.session.MySession
import com.sbgcore.oauth.api.wellknown.wellKnownRoutes
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.basic
import io.ktor.features.*
import io.ktor.http.CacheControl
import io.ktor.http.content.CachingOptions
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Locations
import io.ktor.serialization.serialization
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie

@Suppress("unused") // Inform the IDE that we are actually using this
@KtorExperimentalLocationsAPI
fun Application.main() {

    install(Locations)
    install(AutoHeadResponse)
    install(DataConversion)

    install(HSTS) {
        includeSubDomains = true
    }

    install(ContentNegotiation) {
        serialization()
    }

    install(Sessions) {
        cookie<MySession>("MY_SESSION") {
            cookie.extensions["SameSite"] = "lax"
        }
    }

    install(Compression) {
        gzip {
            priority = 1.0
        }
        deflate {
            priority = 10.0
            minimumSize(1024) // condition
        }
    }

    // Disable caching via headers on all requests
    install(CachingHeaders) {
        // no-store
        options { CachingOptions(CacheControl.NoStore(null)) }
        // no-cache
        options { CachingOptions(CacheControl.NoCache(null)) }
        // must-revalidate, proxy-revalidate, max-age=0
        options {
            CachingOptions(
                CacheControl.MaxAge(
                    maxAgeSeconds = 0,
                    mustRevalidate = true,
                    proxyRevalidate = true
                )
            )
        }
    }

    install(Authentication) {
        basic("client_id_and_secret") {

        }
    }

    // Setup the well known routes
    wellKnownRoutes()

    // TODO - OpenID routes
    // TODO - Account routes
    // TODO - Product transfer routes
}