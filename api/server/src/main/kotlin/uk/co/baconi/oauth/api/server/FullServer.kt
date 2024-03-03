package uk.co.baconi.oauth.api.server

import io.ktor.server.routing.*
import uk.co.baconi.oauth.api.assets.AssetsRoute
import uk.co.baconi.oauth.api.authentication.AuthenticationRoute
import uk.co.baconi.oauth.api.authorisation.AuthorisationCodeService
import uk.co.baconi.oauth.api.authorisation.AuthorisationRoute
import uk.co.baconi.oauth.api.common.*
import uk.co.baconi.oauth.api.common.CommonModule.common
import uk.co.baconi.oauth.api.common.authentication.CustomerAuthenticationService
import uk.co.baconi.oauth.api.common.authentication.CustomerCredentialRepository
import uk.co.baconi.oauth.api.common.authentication.CustomerStatusRepository
import uk.co.baconi.oauth.api.common.authorisation.AuthorisationCodeRepository
import uk.co.baconi.oauth.api.common.client.ClientConfigurationRepository
import uk.co.baconi.oauth.api.common.client.ClientSecretRepository
import uk.co.baconi.oauth.api.common.client.ClientSecretService
import uk.co.baconi.oauth.api.common.scope.ScopeRepository
import uk.co.baconi.oauth.api.common.token.AccessTokenRepository
import uk.co.baconi.oauth.api.common.token.AccessTokenService
import uk.co.baconi.oauth.api.common.token.RefreshTokenRepository
import uk.co.baconi.oauth.api.common.token.RefreshTokenService
import uk.co.baconi.oauth.api.session.info.SessionInfoRoute
import uk.co.baconi.oauth.api.session.info.SessionInfoServer.generateTestRefreshTokens
import uk.co.baconi.oauth.api.session.info.SessionInfoService
import uk.co.baconi.oauth.api.token.*
import uk.co.baconi.oauth.api.token.introspection.IntrospectionRoute
import uk.co.baconi.oauth.api.token.introspection.IntrospectionService

object FullServer : AuthenticationModule, DatabaseModule, AssetsRoute, AuthenticationRoute, AuthorisationRoute, IntrospectionRoute, SessionInfoRoute, TokenRoute, TestAccessTokenModule, TestRefreshTokenModule, TestUserModule {

    override val scopeRepository = ScopeRepository()

    private val accessTokenRepository = AccessTokenRepository(accessTokenDatabase)
    override val accessTokenService = AccessTokenService(accessTokenRepository)

    private val refreshTokenRepository = RefreshTokenRepository(refreshTokenDatabase)
    override val refreshTokenService = RefreshTokenService(refreshTokenRepository)
    override val refreshTokenGrant = RefreshTokenGrant(accessTokenService, refreshTokenService)

    override val assertionGrant = AssertionGrant(accessTokenService, refreshTokenService)

    private val clientSecretRepository = ClientSecretRepository()
    override val clientConfigurationRepository = ClientConfigurationRepository(scopeRepository)
    override val clientSecretService = ClientSecretService(clientSecretRepository, clientConfigurationRepository)

    override val authorisationCodeGrant = AuthorisationCodeGrant(accessTokenService, refreshTokenService)
    override val authorisationCodeRepository = AuthorisationCodeRepository(authorisationCodeDatabase)
    override val authorisationCodeService = AuthorisationCodeService(authorisationCodeRepository)

    override val customerStatusRepository = CustomerStatusRepository(customerStatusDatabase)
    override val customerCredentialRepository = CustomerCredentialRepository(customerCredentialDatabase)
    override val customerAuthenticationService = CustomerAuthenticationService(customerCredentialRepository, customerStatusRepository)

    override val passwordGrant = PasswordGrant(accessTokenService, refreshTokenService, customerAuthenticationService)

    override val introspectionService = IntrospectionService(accessTokenRepository)

    override val sessionInfoService = SessionInfoService(accessTokenRepository, refreshTokenRepository)

    fun start() {
        embeddedCommonServer {
            common()
            authentication()
            routing {
                assets()
                authentication()
                authorisation()
                sessionInfo()
                token()
                introspection()
                // TODO - revocation()
            }
            generateTestAccessTokens()
            generateTestRefreshTokens()
            generateTestUsers()
        }.start(true)
    }
}