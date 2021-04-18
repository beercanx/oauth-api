package com.sbgcore.oauth.api.ktor.auth

import com.sbgcore.oauth.api.client.ClientPrincipal
import com.sbgcore.oauth.api.ktor.ApplicationContext
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.response.*

/**
 * Extracts a client of type [C] before the inner block is called.
 *  - If there is no principal of tha type, then an [IllegalStateException] is thrown.
 */
inline fun <reified C : ClientPrincipal> ApplicationContext.extractClient(block: ApplicationContext.(C) -> Unit) {

    // If the application is setup correctly this should not be null.
    val client = checkNotNull(call.principal<C>()) {
        "${C::class.simpleName} should not be null"
    }

    block(client)
}