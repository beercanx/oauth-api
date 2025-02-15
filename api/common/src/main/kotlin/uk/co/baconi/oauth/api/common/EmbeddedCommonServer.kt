package uk.co.baconi.oauth.api.common

import com.typesafe.config.ConfigFactory
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.cio.CIOApplicationEngine.*
import io.ktor.server.engine.*

fun embeddedCommonServer(module: Application.() -> Unit): EmbeddedServer<CIOApplicationEngine, Configuration> {
    val config = ConfigFactory.load().getConfig("uk.co.baconi.oauth.api.server")
    return embeddedServer(
        CIO,
        configure = {
            connector {
                host = config.getString("host")
                port = config.getInt("port")
            }
            connectionIdleTimeoutSeconds = config.getInt("connectionIdleTimeoutSeconds")
        },
        module = module
    )
}