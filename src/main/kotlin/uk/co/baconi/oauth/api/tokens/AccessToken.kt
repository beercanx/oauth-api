package uk.co.baconi.oauth.api.tokens

import io.ktor.auth.*
import org.dizitart.no2.objects.Id
import uk.co.baconi.oauth.api.client.ClientId
import uk.co.baconi.oauth.api.scopes.Scopes
import java.time.OffsetDateTime
import java.time.OffsetDateTime.now

data class AccessToken(
    @Id override val value: String,
    override val username: String,
    override val clientId: ClientId,
    override val scopes: Set<Scopes>,
    override val issuedAt: OffsetDateTime,
    override val expiresAt: OffsetDateTime,
    override val notBefore: OffsetDateTime
) : Token, Principal {

    companion object

    fun hasExpired(): Boolean = now().isAfter(expiresAt)

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
        if (javaClass != other?.javaClass) return false

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
