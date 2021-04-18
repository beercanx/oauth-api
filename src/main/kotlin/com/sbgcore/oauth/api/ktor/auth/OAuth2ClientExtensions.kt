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
inline fun <reified C : ClientPrincipal> ApplicationContext.requireClient(block: ApplicationContext.(C) -> Unit) {

    // If the application is setup correctly this should not be null.
    val client = checkNotNull(call.principal<C>()) {
        "${C::class.simpleName} should not be null"
    }

    block(client)
}