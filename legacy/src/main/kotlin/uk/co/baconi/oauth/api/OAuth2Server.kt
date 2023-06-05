package uk.co.baconi.oauth.api

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.http.*
import io.ktor.http.auth.HttpAuthHeader.*
import io.ktor.http.auth.HttpAuthHeader.Parameters
import io.ktor.server.routing.*
import io.ktor.server.plugins.autohead.AutoHeadResponse
import io.ktor.server.plugins.cachingheaders.CachingHeaders
import io.ktor.server.plugins.compression.Compression
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.dataconversion.DataConversion
import io.ktor.server.plugins.doublereceive.DoubleReceive
import io.ktor.server.plugins.hsts.HSTS
import io.ktor.server.resources.*
import io.ktor.server.sessions.*
import io.ktor.http.content.CachingOptions
import io.ktor.server.plugins.compression.*
import io.ktor.serialization.kotlinx.json.*
import uk.co.baconi.oauth.api.assets.StaticAssetsRoute
import uk.co.baconi.oauth.api.authentication.AuthenticatedSession
import uk.co.baconi.oauth.api.authentication.AuthenticationRoute
import uk.co.baconi.oauth.api.authentication.AuthenticationService
import uk.co.baconi.oauth.api.authentication.AuthenticationSession
import uk.co.baconi.oauth.api.authorisation.*
import uk.co.baconi.oauth.api.client.*
import uk.co.baconi.oauth.api.customer.CustomerMatchService
import uk.co.baconi.oauth.api.customer.NitriteCustomerCredentialRepository
import uk.co.baconi.oauth.api.customer.NitriteCustomerStatusRepository
import uk.co.baconi.oauth.api.exchange.ExchangeRoute
import uk.co.baconi.oauth.api.exchange.grants.assertion.AssertionRedemptionGrant
import uk.co.baconi.oauth.api.exchange.grants.authorisation.AuthorisationCodeGrant
import uk.co.baconi.oauth.api.exchange.grants.password.PasswordCredentialsGrant
import uk.co.baconi.oauth.api.exchange.grants.refresh.RefreshGrant
import uk.co.baconi.oauth.api.introspection.IntrospectionRoute
import uk.co.baconi.oauth.api.introspection.IntrospectionService
import uk.co.baconi.oauth.api.ktor.auth.basic
import uk.co.baconi.oauth.api.ktor.auth.bearer
import uk.co.baconi.oauth.api.ktor.auth.form
import uk.co.baconi.oauth.api.revocation.RevocationRoute
import uk.co.baconi.oauth.api.scopes.TypesafeScopesConfigurationRepository
import uk.co.baconi.oauth.api.swagger.SwaggerRoute
import uk.co.baconi.oauth.api.tokens.AccessToken
import uk.co.baconi.oauth.api.tokens.AccessTokenService
import uk.co.baconi.oauth.api.tokens.NitriteAccessTokenRepository
import uk.co.baconi.oauth.api.tokens.TokenAuthenticationService
import uk.co.baconi.oauth.api.userinfo.UserInfoRoute
import uk.co.baconi.oauth.api.userinfo.UserInfoService
import uk.co.baconi.oauth.api.wellknown.WellKnownRoute

object OAuth2Server {

    fun Application.module() {

        // Create some session types
        install(Sessions) {
            cookie<AuthenticationSession>("AuthenticationSession", storage = SessionStorageMemory())
            cookie<AuthenticatedSession>("AuthenticatedSession", storage = SessionStorageMemory())
        }

        // Graceful Shutdown
        environment.monitor.subscribe(ApplicationStopped) {
            closeAndLog(clientSecretRepository)
            closeAndLog(accessTokenRepository)
            closeAndLog(customerCredentialRepository)
            closeAndLog(customerStatusRepository)
        }
    }
}
