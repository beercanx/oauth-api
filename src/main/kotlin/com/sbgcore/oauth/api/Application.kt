package com.sbgcore.oauth.api

import com.sbgcore.oauth.api.authentication.AuthenticatedClient
import com.sbgcore.oauth.api.authentication.ErrorResponse
import com.sbgcore.oauth.api.authentication.PkceClient
import com.sbgcore.oauth.api.openid.openIdRoutes
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
import com.sbgcore.oauth.api.ktor.form
import com.sbgcore.oauth.api.openid.openIdRoutesV2
import io.ktor.application.call
import io.ktor.response.header
import io.ktor.response.respond

@Suppress("unused") // Inform the IDE that we are actually using this
@KtorExperimentalLocationsAPI
fun Application.main() {

    install(Locations)
    install(AutoHeadResponse)
    install(DataConversion)

    install(HSTS) {
        includeSubDomains = true
    }

    // OAuth API's don't seem to use JSON input
    //install(ContentNegotiation) {
    //    json()
    //}

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
        basic<AuthenticatedClient> {
            realm = "skybettingandgaming"
            validate { (clientId, clientSecret) ->
                if(clientId.isBlank() || clientSecret.isBlank()) {
                     null
                } else {
                    // TODO - Lookup against client config / database
                    AuthenticatedClient(clientId)
                }
            }
        }
        form<PkceClient> {
            userParamName = "client_id"
            passwordParamName = "client_id"
            validate {  (clientId, _) ->
                if(clientId.isBlank()) {
                    null
                } else {
                    // TODO - Lookup against client config / database
                    PkceClient(clientId)
                }
            }
            challenge {
                call.respond(ErrorResponse("invalid_client"))
            }
        }
    }

    // Setup the well known routes
    wellKnownRoutes()

    // Setup the OpenID connect routes
    openIdRoutesV2()

    // TODO - Account routes
    // TODO - Product transfer routes
}