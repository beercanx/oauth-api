package uk.co.baconi.oauth.api

import io.ktor.application.*
import java.io.Closeable

fun Application.closeAndLog(closeable: Closeable) {
    try {
        log.info("Closing ${closeable::class}")
        closeable.close()
    } catch (exception: Exception) {
        log.error("Exception closing ${closeable::class}", exception)
    }
}