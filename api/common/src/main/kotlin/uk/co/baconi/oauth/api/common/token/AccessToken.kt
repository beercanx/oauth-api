package uk.co.baconi.oauth.api.common.token

import io.ktor.server.auth.*
import uk.co.baconi.oauth.api.common.authentication.AuthenticatedUsername
import uk.co.baconi.oauth.api.common.client.ClientId
import uk.co.baconi.oauth.api.common.scope.Scope
import java.time.Instant
import java.time.Instant.now
import java.util.*

data class AccessToken(
    override val value: UUID,
    override val username: AuthenticatedUsername,
    override val clientId: ClientId,
    override val scopes: Set<Scope>,
    override val issuedAt: Instant, // TODO - Validate if this is the right datetime type to use.
    override val expiresAt: Instant,
    override val notBefore: Instant
) : Token, Principal {

    fun hasExpired(): Boolean = now().isAfter(expiresAt)
    fun isBefore(): Boolean = now().isBefore(notBefore)

    /**
     * Generated to exclude [value] from the toString output.
     */
    override fun toString(): String {
        return "AccessToken(value='REDACTED', username='$username', clientId=$clientId, scopes=$scopes, issuedAt=$issuedAt, expiresAt=$expiresAt, notBefore=$notBefore)"
    }
}
