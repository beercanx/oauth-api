package uk.co.baconi.oauth.api.token.introspection

import io.ktor.server.routing.*
import uk.co.baconi.oauth.api.common.AuthenticationModule
import uk.co.baconi.oauth.api.common.CommonModule.common
import uk.co.baconi.oauth.api.common.DatabaseFactory.accessTokenDatabase
import uk.co.baconi.oauth.api.common.TestAccessTokenModule
import uk.co.baconi.oauth.api.common.client.ClientConfigurationRepository
import uk.co.baconi.oauth.api.common.client.ClientSecretRepository
import uk.co.baconi.oauth.api.common.client.ClientSecretService
import uk.co.baconi.oauth.api.common.embeddedCommonServer
import uk.co.baconi.oauth.api.common.token.AccessTokenRepository
import uk.co.baconi.oauth.api.common.token.AccessTokenService

/**
 * Start a server for just Introspection requests
 */
internal object IntrospectionServer : AuthenticationModule, IntrospectionRoute, TestAccessTokenModule {

    private val accessTokenRepository = AccessTokenRepository(accessTokenDatabase)
    override val accessTokenService = AccessTokenService(accessTokenRepository)

    private val clientSecretRepository = ClientSecretRepository()
    private val clientConfigurationRepository = ClientConfigurationRepository()
    override val clientSecretService = ClientSecretService(clientSecretRepository, clientConfigurationRepository)

    override val introspectionService = IntrospectionService(accessTokenRepository)

    fun start() {
        embeddedCommonServer {
            common()
            authentication()
            routing {
                introspection()
            }
            generateTestAccessTokens()
        }.start(true)
    }
}