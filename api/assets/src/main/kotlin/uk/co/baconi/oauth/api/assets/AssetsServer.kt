package uk.co.baconi.oauth.api.assets

import io.ktor.server.routing.*
import uk.co.baconi.oauth.api.common.CommonModule.common
import uk.co.baconi.oauth.api.common.embeddedCommonServer

/**
 * Start a server for just static Asset requests
 */
internal object AssetsServer : AssetsRoute {

    fun start() {
        embeddedCommonServer {
            common()
            routing {
                assets()
            }
        }.start(true)
    }
}