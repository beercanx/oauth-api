package com.sbgcore.oauth.api

import com.sbgcore.oauth.api.authentication.ClientSecret
import com.sbgcore.oauth.api.authentication.ConfidentialClient
import com.sbgcore.oauth.api.customer.internal.InternalMatchService
import com.sbgcore.oauth.api.customer.internal.NitriteInternalCredentialRepository
import com.sbgcore.oauth.api.ktor.basic
import com.sbgcore.oauth.api.openid.exchange.flows.assertion.AssertionRedemptionFlow
import com.sbgcore.oauth.api.openid.exchange.flows.authorization.AuthorizationCodeFlow
import com.sbgcore.oauth.api.openid.exchange.flows.password.PasswordFlow
import com.sbgcore.oauth.api.openid.exchange.flows.refresh.RefreshFlow
import com.sbgcore.oauth.api.openid.exchange.tokens.AccessTokenService
import com.sbgcore.oauth.api.openid.introspection.IntrospectionService
import com.sbgcore.oauth.api.openid.openIdRoutes
import com.sbgcore.oauth.api.storage.nitrite.NitriteAccessTokenRepository
import com.sbgcore.oauth.api.storage.nitrite.NitriteClientSecretRepository
import com.sbgcore.oauth.api.swagger.swaggerRoutes
import com.sbgcore.oauth.api.wellknown.wellKnownRoutes
import com.typesafe.config.ConfigFactory
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.locations.*
import io.ktor.routing.*
import io.ktor.serialization.*
import org.bouncycastle.crypto.generators.OpenBSDBCrypt

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

    val config = ConfigFactory.load().getConfig("com.sbgcore.oauth.api")

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


    //
    // Dependencies for injection
    //

    // Repositories
    val accessTokenRepository = NitriteAccessTokenRepository()
    val internalCredentialRepository = NitriteInternalCredentialRepository()

    // Services
    val matchService = InternalMatchService(internalCredentialRepository)
    val accessTokenService = AccessTokenService(accessTokenRepository)

    // Flows
    val passwordFlow = PasswordFlow(matchService, accessTokenService)
    val refreshFlow = RefreshFlow()
    val authorizationCodeFlow = AuthorizationCodeFlow()
    val assertionRedemptionFlow = AssertionRedemptionFlow()
    val introspectionService = IntrospectionService(accessTokenRepository)

    routing {
        //
        // Setup the well known routes
        //
        wellKnownRoutes()

        //
        // Setup the OpenID connect routes
        //
        openIdRoutes(passwordFlow, refreshFlow, authorizationCodeFlow, assertionRedemptionFlow, introspectionService)

        // TODO - Account routes
        // TODO - Product transfer routes

        //
        // Swagger UI and spec
        //
        swaggerRoutes()
    }
}