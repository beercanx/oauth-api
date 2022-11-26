package uk.co.baconi.oauth.api.token

import io.ktor.server.routing.*
import uk.co.baconi.oauth.api.common.*
import uk.co.baconi.oauth.api.common.CommonModule.common
import uk.co.baconi.oauth.api.common.DatabaseFactory.accessTokenDatabase
import uk.co.baconi.oauth.api.common.DatabaseFactory.authorisationCodeDatabase
import uk.co.baconi.oauth.api.common.DatabaseFactory.customerCredentialDatabase
import uk.co.baconi.oauth.api.common.DatabaseFactory.customerStatusDatabase
import uk.co.baconi.oauth.api.common.DatabaseFactory.refreshTokenDatabase
import uk.co.baconi.oauth.api.common.authorisation.AuthorisationCodeRepository
import uk.co.baconi.oauth.api.common.client.ClientConfigurationRepository
import uk.co.baconi.oauth.api.common.client.ClientSecretRepository
import uk.co.baconi.oauth.api.common.client.ClientSecretService
import uk.co.baconi.oauth.api.common.token.AccessTokenRepository
import uk.co.baconi.oauth.api.common.token.AccessTokenService
import uk.co.baconi.oauth.api.common.authentication.CustomerAuthenticationService
import uk.co.baconi.oauth.api.common.authentication.CustomerCredentialRepository
import uk.co.baconi.oauth.api.common.authentication.CustomerStatusRepository
import uk.co.baconi.oauth.api.common.token.RefreshTokenRepository
import uk.co.baconi.oauth.api.common.token.RefreshTokenService

/**
 * Start a server for just Introspection requests
 */
internal object TokenServer : AuthenticationModule, TokenRoute, TestAccessTokenModule, TestUserModule {

    private val accessTokenRepository = AccessTokenRepository(accessTokenDatabase)
    override val accessTokenService = AccessTokenService(accessTokenRepository)

    private val refreshTokenRepository = RefreshTokenRepository(refreshTokenDatabase)
    override val refreshTokenService = RefreshTokenService(refreshTokenRepository)
    override val refreshTokenGrant = RefreshTokenGrant(accessTokenService, refreshTokenService)

    private val clientSecretRepository = ClientSecretRepository()
    private val clientConfigurationRepository = ClientConfigurationRepository()
    override val clientSecretService = ClientSecretService(clientSecretRepository, clientConfigurationRepository)

    override val authorisationCodeGrant = AuthorisationCodeGrant(accessTokenService, refreshTokenService)
    override val authorisationCodeRepository = AuthorisationCodeRepository(authorisationCodeDatabase)

    override val customerStatusRepository = CustomerStatusRepository(customerStatusDatabase)

    override val customerCredentialRepository = CustomerCredentialRepository(customerCredentialDatabase)
    private val customerAuthenticationService = CustomerAuthenticationService(customerCredentialRepository, customerStatusRepository)
    override val passwordGrant = PasswordGrant(accessTokenService, refreshTokenService, customerAuthenticationService)

    fun start() {
        embeddedCommonServer {
            common()
            authentication()
            routing {
                token()
            }
            generateTestAccessTokens()
            generateTestUsers()
        }.start(true)
    }

}