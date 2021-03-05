package com.sbgcore.oauth.api

import com.sbgcore.oauth.api.authentication.ClientSecret
import com.sbgcore.oauth.api.authentication.ConfidentialClient
import com.sbgcore.oauth.api.ktor.basic
import com.sbgcore.oauth.api.openid.ClientId
import com.sbgcore.oauth.api.openid.openIdRoutes
import com.sbgcore.oauth.api.storage.NitriteClientSecretRepository
import com.sbgcore.oauth.api.wellknown.wellKnownRoutes
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.locations.*
import io.ktor.serialization.*
import org.bouncycastle.crypto.generators.OpenBSDBCrypt
import org.slf4j.LoggerFactory

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

    val clientSecretRepository = NitriteClientSecretRepository()

    install(Authentication) {
        basic<ConfidentialClient> {
            realm = "skybettingandgaming"
            validate { (clientId, clientSecret) ->
                // TODO - Refactor into testable unit
                clientSecretRepository
                    .findAllByClientId(clientId)
                    .filter { secret ->
                        OpenBSDBCrypt.checkPassword(secret.secret, clientSecret.toCharArray())
                    }
                    .map(ClientSecret::clientId)
                    .map(::ConfidentialClient)
                    .firstOrNull()
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