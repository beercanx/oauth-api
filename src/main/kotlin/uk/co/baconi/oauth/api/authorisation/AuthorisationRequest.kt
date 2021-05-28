package uk.co.baconi.oauth.api.authorisation

import uk.co.baconi.oauth.api.client.ClientId
import uk.co.baconi.oauth.api.scopes.Scopes

sealed class AuthorisationRequest {

    // TODO - Add failure reasons
    data class Invalid(val error: String, val description: String) : AuthorisationRequest()

    data class Aborted(val redirectUri: String) : AuthorisationRequest()

    data class Valid(
        val responseType: AuthorisationResponseType,
        val clientId: ClientId,
        val redirectUri: String,
        val state: String,
        val requestedScope: Set<Scopes>
    ) : AuthorisationRequest() {

        /**
         * Generated to exclude [state] from the toString output.
         */
        override fun toString(): String {
            return "Valid(responseType=$responseType, clientId=$clientId, redirectUri=$redirectUri, state='REDACTED', requestedScope=$requestedScope)"
        }
    }
}