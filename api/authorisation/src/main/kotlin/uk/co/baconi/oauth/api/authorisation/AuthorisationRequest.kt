package uk.co.baconi.oauth.api.authorisation

import uk.co.baconi.oauth.api.common.authorisation.AuthorisationResponseType
import uk.co.baconi.oauth.api.common.client.ClientId
import uk.co.baconi.oauth.api.common.scope.Scope

sealed class AuthorisationRequest {

    data class InvalidClient(val reason: String) : AuthorisationRequest()

    data class InvalidRedirect(val reason: String) : AuthorisationRequest()

    data class Invalid(val redirectUri: String, val error: String, val description: String, val state: String?) : AuthorisationRequest()

    // TODO - Add PKCE support?

    data class Valid(
        val responseType: AuthorisationResponseType,
        val clientId: ClientId,
        val redirectUri: String,
        val state: String,
        val scopes: Set<Scope>
    ) : AuthorisationRequest() {

        /**
         * Generated to exclude [state] from the toString output.
         */
        override fun toString(): String {
            return "Valid(responseType=$responseType, clientId=$clientId, redirectUri=$redirectUri, state='REDACTED', scopes=$scopes)"
        }
    }
}
