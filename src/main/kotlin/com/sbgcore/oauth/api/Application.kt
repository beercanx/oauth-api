package com.sbgcore.oauth.api

import com.sbgcore.oauth.api.client.ClientAuthenticationService
import com.sbgcore.oauth.api.client.ConfidentialClient
import com.sbgcore.oauth.api.client.NitriteClientSecretRepository
import com.sbgcore.oauth.api.client.TypesafeClientConfigurationRepository
import com.sbgcore.oauth.api.customer.CustomerMatchService
import com.sbgcore.oauth.api.customer.NitriteCustomerCredentialRepository
import com.sbgcore.oauth.api.customer.NitriteCustomerStatusRepository
import com.sbgcore.oauth.api.ktor.auth.basic
import com.sbgcore.oauth.api.ktor.auth.oAuth2Bearer
import com.sbgcore.oauth.api.openid.TypesafeScopesConfigurationRepository
import com.sbgcore.oauth.api.openid.exchange.flows.assertion.AssertionRedemptionFlow
import com.sbgcore.oauth.api.openid.exchange.flows.authorization.AuthorizationCodeFlow
import com.sbgcore.oauth.api.openid.exchange.flows.password.PasswordFlow
import com.sbgcore.oauth.api.openid.exchange.flows.refresh.RefreshFlow
import com.sbgcore.oauth.api.openid.introspection.IntrospectionService
import com.sbgcore.oauth.api.openid.openIdRoutes
import com.sbgcore.oauth.api.openid.userinfo.UserInfoService
import com.sbgcore.oauth.api.swagger.swaggerRoutes
import com.sbgcore.oauth.api.tokens.AccessToken
import com.sbgcore.oauth.api.tokens.AccessTokenService
import com.sbgcore.oauth.api.tokens.NitriteAccessTokenRepository
import com.sbgcore.oauth.api.tokens.TokenAuthenticationService
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
    val clientConfigurationRepository = TypesafeClientConfigurationRepository()
    val clientAuthenticationService = ClientAuthenticationService(clientSecretRepository, clientConfigurationRepository)

    // Tokens
    val accessTokenRepository = NitriteAccessTokenRepository()
    val accessTokenService = AccessTokenService(accessTokenRepository)
    val introspectionService = IntrospectionService(accessTokenRepository)
    val tokenAuthenticationService = TokenAuthenticationService(accessTokenRepository)
    val scopesConfigurationRepository = TypesafeScopesConfigurationRepository()

    // Customer
    val customerCredentialRepository = NitriteCustomerCredentialRepository()
    val customerStatusRepository = NitriteCustomerStatusRepository()
    val customerMatchService = CustomerMatchService(customerCredentialRepository)
    val userInfoService = UserInfoService(scopesConfigurationRepository)

    // Flows
    val passwordFlow = PasswordFlow(customerMatchService, accessTokenService)
    val refreshFlow = RefreshFlow()
    val authorizationCodeFlow = AuthorizationCodeFlow()
    val assertionRedemptionFlow = AssertionRedemptionFlow()

    // Graceful Shutdown
    environment.monitor.subscribe(ApplicationStopped) {
        closeAndLog(clientSecretRepository)
        closeAndLog(accessTokenRepository)
        closeAndLog(customerCredentialRepository)
        closeAndLog(customerStatusRepository)
    }

    install(Authentication) {
        basic<ConfidentialClient> {
            realm = "skybettingandgaming"
            validate { (clientId, clientSecret) ->
                clientAuthenticationService.confidentialClient(clientId, clientSecret)
            }
        }
        oAuth2Bearer<AccessToken> {
            realm = "skybettingandgaming"
            validate { (token) ->
                tokenAuthenticationService.accessToken(token)
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
            introspectionService,
            userInfoService
        )

        // TODO - Account routes
        // TODO - Product transfer routes

        //
        // Swagger UI and spec
        //
        swaggerRoutes()

        //
        // Setup some generic static assets
        //
        static("assets") {
            resources("assets")
        }
    }
}