package uk.co.baconi.oauth.api.token

import uk.co.baconi.oauth.api.common.client.ConfidentialClient
import uk.co.baconi.oauth.api.common.scope.Scope

data class PasswordRequest(
    override val principal: ConfidentialClient,
    val scopes: Set<Scope>,
    val username: String,
    val password: CharArray // Requires manually generated equals/hashCode
) : TokenRequest.Valid<ConfidentialClient> {
    override fun toString(): String {
        return "PasswordRequest(principal=$principal, scopes=$scopes, username='$username', password='REDACTED')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PasswordRequest

        if (principal != other.principal) return false
        if (scopes != other.scopes) return false
        if (username != other.username) return false
        if (!password.contentEquals(other.password)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = principal.hashCode()
        result = 31 * result + scopes.hashCode()
        result = 31 * result + username.hashCode()
        result = 31 * result + password.contentHashCode()
        return result
    }
}