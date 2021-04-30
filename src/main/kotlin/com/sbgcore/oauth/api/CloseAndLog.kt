package com.sbgcore.oauth.api

import io.ktor.application.*
import java.io.Closeable

fun Application.closeAndLog(closeable: Closeable) {
    try {
        closeable.close()
    } catch (exception: Exception) {
        log.error("Exception while closing ${closeable::class}", exception)
    }
}