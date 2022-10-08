package uk.co.baconi.oauth.api.ktor.auth

import io.ktor.application.*
import io.ktor.auth.*
import uk.co.baconi.oauth.api.client.ClientPrincipal
import uk.co.baconi.oauth.api.ktor.ApplicationContext

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