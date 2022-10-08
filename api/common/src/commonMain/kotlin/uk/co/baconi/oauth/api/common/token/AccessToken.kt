package uk.co.baconi.oauth.api.common.token

import io.ktor.server.auth.*
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import uk.co.baconi.oauth.api.common.client.ClientId
import uk.co.baconi.oauth.api.common.datetime.isAfter
import uk.co.baconi.oauth.api.common.scope.Scope

data class AccessToken(
    override val value: String,
    override val username: String,
    override val clientId: ClientId,
    override val scopes: Set<Scope>,
    override val issuedAt: LocalDateTime, // TODO - Setup timezone handling and pinning
    override val expiresAt: LocalDateTime, // TODO - Setup timezone handling and pinning
    override val notBefore: LocalDateTime // TODO - Setup timezone handling and pinning
) : Token, Principal {

    companion object

    fun hasExpired(): Boolean = Clock.System.now().isAfter(expiresAt)

    /**
     * Generated to exclude [value] from the toString output.
     */
    override fun toString(): String {
        return "AccessToken(value=REDACTED, username='$username', clientId=$clientId, scopes=$scopes, issuedAt=$issuedAt, expiresAt=$expiresAt, notBefore=$notBefore)"
    }

    /**
     * Generated based on its database ID field [value].
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as AccessToken

        if (value != other.value) return false

        return true
    }

    /**
     * Generated based on its database ID field [value].
     */
    override fun hashCode(): Int {
        return value.hashCode()
    }
}
