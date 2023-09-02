package uk.co.baconi.oauth.api.authorisation

import uk.co.baconi.oauth.api.common.authorisation.AuthorisationResponseType
import uk.co.baconi.oauth.api.common.authorisation.CodeChallenge
import uk.co.baconi.oauth.api.common.authorisation.CodeChallengeMethod
import uk.co.baconi.oauth.api.common.client.ClientId
import uk.co.baconi.oauth.api.common.scope.Scope

sealed class AuthorisationRequest {

    data class InvalidClient(val reason: String) : AuthorisationRequest()

    data class InvalidRedirect(val reason: String) : AuthorisationRequest()

    data class Invalid(
        val redirectUri: String,
        val error: String,
        val description: String,
        val state: String?
    ) : AuthorisationRequest() {

        /**
         * Generated to exclude [state] from the toString output.
         */
        override fun toString(): String {
            return "Invalid(redirectUri='$redirectUri', error='$error', description='$description', state='REDACTED')"
        }
    }

    sealed interface Valid {
        val responseType: AuthorisationResponseType
        val clientId: ClientId
        val redirectUri: String
        val state: String
        val scopes: Set<Scope>
    }

    /**
     * Generated to exclude [state] from the toString output.
     */
    data class Basic(
        override val responseType: AuthorisationResponseType,
        override val clientId: ClientId,
        override val redirectUri: String,
        override val state: String,
        override val scopes: Set<Scope>
    ) : Valid, AuthorisationRequest() {
        override fun toString(): String {
            return "AuthorisationRequest.Basic(responseType=$responseType, clientId=$clientId, redirectUri='$redirectUri', state='REDACTED', scopes=$scopes)"
        }
    }

    data class PKCE(
        private val base: Basic,
        val codeChallenge: CodeChallenge,
        val codeChallengeMethod: CodeChallengeMethod
    ) : Valid by base, AuthorisationRequest() {

        /**
         * Generated to exclude [codeChallenge] from the toString output.
         */
        override fun toString(): String {
            return "AuthorisationRequest.PKCE(base=$base, codeChallenge='REDACTED', codeChallengeMethod=$codeChallengeMethod)"
        }
    }
}
