package com.sbgcore.oauth.api

import com.sbgcore.oauth.api.authentication.ClientAuthenticationService
import com.sbgcore.oauth.api.authentication.ConfidentialClient
import com.sbgcore.oauth.api.customer.internal.CustomerMatchService
import com.sbgcore.oauth.api.customer.internal.NitriteCustomerCredentialRepository
import com.sbgcore.oauth.api.ktor.basic
import com.sbgcore.oauth.api.openid.exchange.flows.assertion.AssertionRedemptionFlow
import com.sbgcore.oauth.api.openid.exchange.flows.authorization.AuthorizationCodeFlow
import com.sbgcore.oauth.api.openid.exchange.flows.password.PasswordFlow
import com.sbgcore.oauth.api.openid.exchange.flows.refresh.RefreshFlow
import com.sbgcore.oauth.api.tokens.AccessTokenService
import com.sbgcore.oauth.api.openid.introspection.IntrospectionService
import com.sbgcore.oauth.api.openid.openIdRoutes
import com.sbgcore.oauth.api.tokens.NitriteAccessTokenRepository
import com.sbgcore.oauth.api.authentication.NitriteClientSecretRepository
import com.sbgcore.oauth.api.client.StaticClientConfigurationRepository
import com.sbgcore.oauth.api.swagger.swaggerRoutes
import com.sbgcore.oauth.api.wellknown.wellKnownRoutes
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.locations.*
import io.ktor.routing.*
import io.ktor.serialization.*

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

    //
    // Dependencies for injection
    //

    // Clients
    val clientSecretRepository = NitriteClientSecretRepository()
    val clientConfigurationRepository = StaticClientConfigurationRepository()
    val clientAuthenticationService = ClientAuthenticationService(clientSecretRepository, clientConfigurationRepository)

    // Tokens
    val accessTokenRepository = NitriteAccessTokenRepository()
    val accessTokenService = AccessTokenService(accessTokenRepository)
    val introspectionService = IntrospectionService(accessTokenRepository)

    // Customer
    val customerCredentialRepository = NitriteCustomerCredentialRepository()
    val customerMatchService = CustomerMatchService(customerCredentialRepository)

    // Flows
    val passwordFlow = PasswordFlow(customerMatchService, accessTokenService)
    val refreshFlow = RefreshFlow()
    val authorizationCodeFlow = AuthorizationCodeFlow()
    val assertionRedemptionFlow = AssertionRedemptionFlow()

    install(Authentication) {
        basic<ConfidentialClient> {
            realm = "skybettingandgaming"
            validate { (clientId, clientSecret) ->
                clientAuthenticationService.confidentialClient(clientId, clientSecret)
            }
        }
    }

    routing {
        //
        // Setup the well known routes
        //
        wellKnownRoutes()

        //
        // Setup the OpenID connect routes
        //
        openIdRoutes(
            clientAuthenticationService,
            passwordFlow,
            refreshFlow,
            authorizationCodeFlow,
            assertionRedemptionFlow,
            introspectionService
        )

        // TODO - Account routes
        // TODO - Product transfer routes

        //
        // Swagger UI and spec
        //
        swaggerRoutes()
    }
}