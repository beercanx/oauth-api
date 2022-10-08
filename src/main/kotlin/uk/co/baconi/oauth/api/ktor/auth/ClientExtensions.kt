package uk.co.baconi.oauth.api.ktor.auth

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.response.*
import uk.co.baconi.oauth.api.client.ClientPrincipal
import uk.co.baconi.oauth.api.ktor.ApplicationContext

/**
 * Extracts a client of type [C] before the inner block is called.
 *  - If there is no principal of tha type, then an [IllegalStateException] is thrown.
 */
suspend inline fun <reified C : ClientPrincipal> ApplicationContext.extractClient(block: ApplicationContext.(C) -> Unit) {

    when(val client = call.principal<C>()) {

        // If the application is setup correctly this should not be null.
        null -> {
            application.log.error("${C::class.simpleName} should not be null, there must be a coding mistake somewhere.")
            call.respond(InternalServerError)
        }

        else -> block(client)
    }
}