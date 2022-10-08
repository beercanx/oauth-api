package uk.co.baconi.oauth.api.server

import io.ktor.server.routing.*
import uk.co.baconi.oauth.api.common.AuthenticationModule
import uk.co.baconi.oauth.api.common.CommonModule.common
import uk.co.baconi.oauth.api.common.DatabaseModule.accessTokenDatabase
import uk.co.baconi.oauth.api.common.TestDataModule
import uk.co.baconi.oauth.api.common.client.ClientConfigurationRepository
import uk.co.baconi.oauth.api.common.client.ClientSecretRepository
import uk.co.baconi.oauth.api.common.client.ClientSecretService
import uk.co.baconi.oauth.api.common.embeddedCommonServer
import uk.co.baconi.oauth.api.common.token.AccessTokenRepository
import uk.co.baconi.oauth.api.common.token.AccessTokenService
import uk.co.baconi.oauth.api.token.introspection.IntrospectionRoute
import uk.co.baconi.oauth.api.token.introspection.IntrospectionService

object FullServer : AuthenticationModule, IntrospectionRoute, TestDataModule {

    // TODO - Configure to be an external datasource
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
            accessTokenDatabase()
            routing {
                introspection()
            }
            generateTestData() // TODO - Remove
        }.start(true)
    }
}