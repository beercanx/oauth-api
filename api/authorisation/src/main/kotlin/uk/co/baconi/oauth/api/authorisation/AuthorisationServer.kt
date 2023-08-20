package uk.co.baconi.oauth.api.authorisation

import io.ktor.server.routing.*
import uk.co.baconi.oauth.api.common.CommonModule.common
import uk.co.baconi.oauth.api.common.DatabaseModule
import uk.co.baconi.oauth.api.common.authorisation.AuthorisationCodeRepository
import uk.co.baconi.oauth.api.common.client.ClientConfigurationRepository
import uk.co.baconi.oauth.api.common.embeddedCommonServer
import uk.co.baconi.oauth.api.common.scope.ScopeRepository

/**
 * Start a server for just Authorisation requests
 */
internal object AuthorisationServer : DatabaseModule, AuthorisationRoute {

    override val scopeRepository = ScopeRepository()

    private val authorisationCodeRepository = AuthorisationCodeRepository(authorisationCodeDatabase)
    override val authorisationCodeService = AuthorisationCodeService(authorisationCodeRepository)

    override val clientConfigurationRepository = ClientConfigurationRepository(scopeRepository)

    fun start() {
        embeddedCommonServer {
            common()
            routing {
                authorisation()
            }
        }.start(true)
    }
}