package com.sbgcore.oauth.api

import com.sbgcore.oauth.api.authentication.ConfidentialClient
import com.sbgcore.oauth.api.wellknown.wellKnownRoutes
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.features.*
import io.ktor.http.CacheControl
import io.ktor.http.content.CachingOptions
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Locations
import com.sbgcore.oauth.api.ktor.basic
import com.sbgcore.oauth.api.openid.openIdRoutes
import io.ktor.serialization.json

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
        json()
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
        basic<ConfidentialClient> {
            realm = "skybettingandgaming"
            validate { (clientId, clientSecret) ->
                if(clientId.isBlank() || clientSecret.isBlank()) {
                     null
                } else {
                    // TODO - Lookup against client config / database
                    ConfidentialClient(clientId)
                }
            }
        }
    }

    // Setup the well known routes
    wellKnownRoutes()

    // Setup the OpenID connect routes
    openIdRoutes()

    // TODO - Account routes
    // TODO - Product transfer routes
}