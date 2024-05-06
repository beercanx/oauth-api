package uk.co.baconi.oauth.api.common.authorisation

import uk.co.baconi.oauth.api.common.authentication.AuthenticatedUsername
import uk.co.baconi.oauth.api.common.client.ClientId
import uk.co.baconi.oauth.api.common.scope.Scope
import java.time.Instant
import java.time.Instant.now
import java.util.*

sealed interface AuthorisationCode {

    val value: UUID
    val issuedAt: Instant
    val expiresAt: Instant
    val clientId: ClientId
    val username: AuthenticatedUsername
    val redirectUri: String
    val scopes: Set<Scope>
    val state: String
    val used: Boolean
    fun hasExpired(): Boolean = now().isAfter(expiresAt)

    data class Basic(
        override val value: UUID,
        override val issuedAt: Instant,
        override val expiresAt: Instant,
        override val clientId: ClientId,
        override val username: AuthenticatedUsername,
        override val redirectUri: String,
        override val scopes: Set<Scope>,
        override val state: String,
        override val used: Boolean = false,
    ) : AuthorisationCode {
        override fun toString(): String {
            return "AuthorisationCode.Basic(value='REDACTED', issuedAt=$issuedAt, expiresAt=$expiresAt, clientId=$clientId, username=$username, redirectUri='$redirectUri', scopes=$scopes, state='REDACTED', used=$used)"
        }
    }

    data class PKCE(
        private val base: Basic,
        val codeChallenge: CodeChallenge,
        val codeChallengeMethod: CodeChallengeMethod,
    ) : AuthorisationCode by base {
        override fun toString(): String {
            return "AuthorisationCode.PKCE(value='REDACTED', issuedAt=$issuedAt, expiresAt=$expiresAt, clientId=$clientId, username=$username, redirectUri='$redirectUri', scopes=$scopes, state='REDACTED', used=$used, codeChallenge='REDACTED', codeChallengeMethod=$codeChallengeMethod)"
        }
    }
}
