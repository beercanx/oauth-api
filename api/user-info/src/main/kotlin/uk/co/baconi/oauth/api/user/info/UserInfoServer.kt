package uk.co.baconi.oauth.api.user.info

import io.ktor.server.routing.*
import uk.co.baconi.oauth.api.common.AuthenticationModule
import uk.co.baconi.oauth.api.common.CommonModule.common
import uk.co.baconi.oauth.api.common.DatabaseModule
import uk.co.baconi.oauth.api.common.TestAccessTokenModule
import uk.co.baconi.oauth.api.common.client.ClientConfigurationRepository
import uk.co.baconi.oauth.api.common.client.ClientSecretRepository
import uk.co.baconi.oauth.api.common.client.ClientSecretService
import uk.co.baconi.oauth.api.common.embeddedCommonServer
import uk.co.baconi.oauth.api.common.scope.ScopeConfigurationRepository
import uk.co.baconi.oauth.api.common.token.AccessTokenRepository
import uk.co.baconi.oauth.api.common.token.AccessTokenService

/**
 * Start a server for just Introspection requests
 */
internal object UserInfoServer : AuthenticationModule, DatabaseModule, UserInfoRoute, TestAccessTokenModule {

    private val accessTokenRepository = AccessTokenRepository(accessTokenDatabase)
    override val accessTokenService = AccessTokenService(accessTokenRepository)

    private val clientSecretRepository = ClientSecretRepository()
    private val clientConfigurationRepository = ClientConfigurationRepository()
    override val clientSecretService = ClientSecretService(clientSecretRepository, clientConfigurationRepository)

    private val scopeConfigurationRepository = ScopeConfigurationRepository()
    override val userInfoService = UserInfoService(scopeConfigurationRepository)

    fun start() {
        embeddedCommonServer {
            common()
            authentication()
            routing {
                userInfo()
            }
            generateTestAccessTokens()
        }.start(true)
    }
}