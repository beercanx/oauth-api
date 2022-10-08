package uk.co.baconi.oauth.api.server

import io.ktor.server.cio.*
import io.ktor.server.engine.*
import uk.co.baconi.oauth.api.common.CommonModule.common
import uk.co.baconi.oauth.api.common.DatabaseModule.accessTokenDatabase

fun main() {
    embeddedServer(CIO,
        host = "0.0.0.0",
        port = 8080,
        configure = {
            connectionIdleTimeoutSeconds = 45
        },
        module = {
            common()
            accessTokenDatabase()
        }
    ).start(true)
}
