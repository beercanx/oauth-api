package uk.co.baconi.oauth.api.authorization

import uk.co.baconi.oauth.api.client.ClientId
import uk.co.baconi.oauth.api.scopes.Scopes
import java.net.URI

sealed class AuthorizationRequest {

    object Invalid : AuthorizationRequest()

    data class Valid(
        val responseType: ResponseType,
        val clientId: ClientId,
        val redirectUri: URI,
        val state: String,
        val requestedScope: Set<Scopes>
    ) : AuthorizationRequest() {

        /**
         * Generated to exclude [state] from the toString output.
         */
        override fun toString(): String {
            return "Valid(responseType=$responseType, clientId=$clientId, redirectUri=$redirectUri, state='REDACTED', requestedScope=$requestedScope)"
        }
    }
}