package uk.co.baconi.oauth.api

import uk.co.baconi.oauth.api.client.ClientAuthenticationService
import uk.co.baconi.oauth.api.client.ConfidentialClient
import uk.co.baconi.oauth.api.client.NitriteClientSecretRepository
import uk.co.baconi.oauth.api.client.TypesafeClientConfigurationRepository
import uk.co.baconi.oauth.api.customer.CustomerMatchService
import uk.co.baconi.oauth.api.customer.NitriteCustomerCredentialRepository
import uk.co.baconi.oauth.api.customer.NitriteCustomerStatusRepository
import uk.co.baconi.oauth.api.ktor.auth.basic
import uk.co.baconi.oauth.api.ktor.auth.oAuth2Bearer
import uk.co.baconi.oauth.api.openid.OAuthRoutes
import uk.co.baconi.oauth.api.openid.TypesafeScopesConfigurationRepository
import uk.co.baconi.oauth.api.openid.exchange.grants.assertion.AssertionRedemptionGrant
import uk.co.baconi.oauth.api.openid.exchange.grants.authorization.AuthorizationCodeGrant
import uk.co.baconi.oauth.api.openid.exchange.grants.password.PasswordCredentialsGrant
import uk.co.baconi.oauth.api.openid.exchange.grants.refresh.RefreshGrant
import uk.co.baconi.oauth.api.openid.introspection.IntrospectionService
import uk.co.baconi.oauth.api.openid.userinfo.UserInfoService
import uk.co.baconi.oauth.api.swagger.SwaggerRoutes
import uk.co.baconi.oauth.api.tokens.AccessToken
import uk.co.baconi.oauth.api.tokens.AccessTokenService
import uk.co.baconi.oauth.api.tokens.NitriteAccessTokenRepository
import uk.co.baconi.oauth.api.tokens.TokenAuthenticationService
import uk.co.baconi.oauth.api.wellknown.WellKnownRoutes
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
object OAuth2Server : WellKnownRoutes, OAuthRoutes, SwaggerRoutes, StaticAssetRoutes {

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

    // OAuth Grants
    override val passwordCredentialsGrant = PasswordCredentialsGrant(customerMatchService, accessTokenService)
    override val refreshGrant = RefreshGrant()
    override val authorizationCodeGrant = AuthorizationCodeGrant()
    override val assertionRedemptionGrant = AssertionRedemptionGrant()

    fun Application.module() {

        install(Locations) {}
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
            staticAssets()
        }
    }
}
