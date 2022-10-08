package uk.co.baconi.oauth.api.token

import io.ktor.server.routing.*
import uk.co.baconi.oauth.api.common.AuthenticationModule
import uk.co.baconi.oauth.api.common.CommonModule.common
import uk.co.baconi.oauth.api.common.DatabaseFactory
import uk.co.baconi.oauth.api.common.DatabaseFactory.getAccessTokenDatabase
import uk.co.baconi.oauth.api.common.DatabaseFactory.getAuthorisationCodeDatabase
import uk.co.baconi.oauth.api.common.DatabaseFactory.getCustomerCredentialDatabase
import uk.co.baconi.oauth.api.common.TestDataModule
import uk.co.baconi.oauth.api.common.authorisation.AuthorisationCodeRepository
import uk.co.baconi.oauth.api.common.client.ClientConfigurationRepository
import uk.co.baconi.oauth.api.common.client.ClientSecretRepository
import uk.co.baconi.oauth.api.common.client.ClientSecretService
import uk.co.baconi.oauth.api.common.embeddedCommonServer
import uk.co.baconi.oauth.api.common.token.AccessTokenRepository
import uk.co.baconi.oauth.api.common.token.AccessTokenService
import uk.co.baconi.oauth.common.authentication.CustomerAuthenticationService
import uk.co.baconi.oauth.common.authentication.CustomerCredentialRepository
import uk.co.baconi.oauth.common.authentication.CustomerStatusRepository

/**
 * Start a server for just Introspection requests
 */
internal object TokenServer : AuthenticationModule, TokenRoute, TestDataModule {

    private val accessTokenRepository = AccessTokenRepository(getAccessTokenDatabase())
    override val accessTokenService = AccessTokenService(accessTokenRepository)

    private val clientSecretRepository = ClientSecretRepository()
    private val clientConfigurationRepository = ClientConfigurationRepository()
    override val clientSecretService = ClientSecretService(clientSecretRepository, clientConfigurationRepository)

    override val authorisationCodeGrant = AuthorisationCodeGrant(accessTokenService)
    override val authorisationCodeRepository = AuthorisationCodeRepository(getAuthorisationCodeDatabase())

    private val customerStatusDatabase = DatabaseFactory.getCustomerStatusDatabase()
    private val customerStatusRepository = CustomerStatusRepository(customerStatusDatabase)

    private val customerCredentialRepository = CustomerCredentialRepository(getCustomerCredentialDatabase())
    private val customerAuthenticationService = CustomerAuthenticationService(customerCredentialRepository, customerStatusRepository)
    override val passwordGrant = PasswordGrant(accessTokenService, customerAuthenticationService)

    fun start() {
        embeddedCommonServer {
            common()
            authentication()
            routing {
                token()
            }
            generateTestData() // TODO - Remove
        }.start(true)
    }

}