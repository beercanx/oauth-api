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
import com.sbgcore.oauth.api.openid.OAuthRoutes
import com.sbgcore.oauth.api.openid.TypesafeScopesConfigurationRepository
import com.sbgcore.oauth.api.openid.exchange.flows.assertion.AssertionRedemptionFlow
import com.sbgcore.oauth.api.openid.exchange.flows.authorization.AuthorizationCodeFlow
import com.sbgcore.oauth.api.openid.exchange.flows.password.PasswordFlow
import com.sbgcore.oauth.api.openid.exchange.flows.refresh.RefreshFlow
import com.sbgcore.oauth.api.openid.introspection.IntrospectionService
import com.sbgcore.oauth.api.openid.userinfo.UserInfoService
import com.sbgcore.oauth.api.swagger.SwaggerRoutes
import com.sbgcore.oauth.api.tokens.AccessToken
import com.sbgcore.oauth.api.tokens.AccessTokenService
import com.sbgcore.oauth.api.tokens.NitriteAccessTokenRepository
import com.sbgcore.oauth.api.tokens.TokenAuthenticationService
import com.sbgcore.oauth.api.wellknown.WellKnownRoutes
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
object OAuth2Server : WellKnownRoutes, OAuthRoutes, SwaggerRoutes {

    const val REALM = "oauth-api"

    // Clients
    val clientSecretRepository = NitriteClientSecretRepository()
    val clientConfigurationRepository = TypesafeClientConfigurationRepository()
    override val clientAuthService = ClientAuthenticationService(clientSecretRepository, clientConfigurationRepository)

    // Tokens
    val accessTokenRepository = NitriteAccessTokenRepository()
    val accessTokenService = AccessTokenService(accessTokenRepository)
    override val introspectionService = IntrospectionService(accessTokenRepository)
    val tokenAuthenticationService = TokenAuthenticationService(accessTokenRepository)
    val scopesConfigurationRepository = TypesafeScopesConfigurationRepository()

    // Customer
    val customerCredentialRepository = NitriteCustomerCredentialRepository()
    val customerStatusRepository = NitriteCustomerStatusRepository()
    val customerMatchService = CustomerMatchService(customerCredentialRepository)
    override val userInfoService = UserInfoService(scopesConfigurationRepository)

    // Flows
    override val passwordFlow = PasswordFlow(customerMatchService, accessTokenService)
    override val refreshFlow = RefreshFlow()
    override val authorizationCodeFlow = AuthorizationCodeFlow()
    override val assertionRedemptionFlow = AssertionRedemptionFlow()

    fun Application.module() {

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

        // Graceful Shutdown
        environment.monitor.subscribe(ApplicationStopped) {
            closeAndLog(clientSecretRepository)
            closeAndLog(accessTokenRepository)
            closeAndLog(customerCredentialRepository)
            closeAndLog(customerStatusRepository)
        }

        install(Authentication) {
            basic<ConfidentialClient> {
                realm = REALM
                validate { (clientId, clientSecret) ->
                    clientAuthService.confidentialClient(clientId, clientSecret)
                }
            }
            oAuth2Bearer<AccessToken> {
                realm = REALM
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
            // Setup the OAuth routes
            //
            oAuthRoutes()

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
}
