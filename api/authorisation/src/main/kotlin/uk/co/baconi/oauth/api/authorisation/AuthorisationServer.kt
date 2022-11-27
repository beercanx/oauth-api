package uk.co.baconi.oauth.api.authorisation

import io.ktor.server.routing.*
import uk.co.baconi.oauth.api.common.CommonModule.common
import uk.co.baconi.oauth.api.common.embeddedCommonServer

/**
 * Start a server for just Authorisation requests
 */
internal object AuthorisationServer : AuthorisationRoute {

    fun start() {
        embeddedCommonServer {
            common()
            routing {
                authorisation()
            }
        }.start(true)
    }
}