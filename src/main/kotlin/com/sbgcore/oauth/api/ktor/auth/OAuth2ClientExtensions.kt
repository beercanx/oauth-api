package com.sbgcore.oauth.api.ktor.auth

import com.sbgcore.oauth.api.client.ClientPrincipal
import com.sbgcore.oauth.api.ktor.ApplicationContext
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.response.*

/**
 * Requires a client of type [C] before the inner block is called, if there isn't one it will respond with a 500.
 * This is because its intended to be used inside an [authenticate] block.
 */
suspend inline fun <reified C : ClientPrincipal> ApplicationContext.requireClient(block: ApplicationContext.(C) -> Unit) {
    when (val client = call.principal<C>()) {
        null -> call.respond(HttpStatusCode.InternalServerError)
        else -> block(client)
    }
}