package uk.co.baconi.oauth.api.common.ktor.auth

import io.ktor.server.application.*
import io.ktor.server.auth.*
import uk.co.baconi.oauth.api.common.client.ClientPrincipal

/**
 * Extracts a client of type [C] before the inner block is called.
 *  - If there is no principal of tha type, then an [IllegalStateException] is thrown.
 */
inline fun <reified C : ClientPrincipal> ApplicationCall.extractClient(): C {
    return checkNotNull(principal()) {
        "${C::class.simpleName} should not be null, there must be a coding mistake somewhere."
    }
}