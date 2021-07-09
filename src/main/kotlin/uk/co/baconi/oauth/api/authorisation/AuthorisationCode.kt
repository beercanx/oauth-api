package uk.co.baconi.oauth.api.authorisation

import org.dizitart.no2.objects.Id
import uk.co.baconi.oauth.api.authentication.AuthenticatedUsername
import uk.co.baconi.oauth.api.client.ClientId
import uk.co.baconi.oauth.api.scopes.Scopes
import java.time.OffsetDateTime
import java.time.OffsetDateTime.now

data class AuthorisationCode(
    @Id val value: String,
    val issuedAt: OffsetDateTime,
    val expiresAt: OffsetDateTime,
    val clientId: ClientId,
    val username: AuthenticatedUsername,
    val redirectUri: String,
    val requestedScope: Set<Scopes>
) {

    /**
     * Returns true if the [AuthorisationCode] has now expired.
     */
    fun hasExpired(): Boolean = now().isAfter(expiresAt)

    /**
     * Generated to exclude [value] from the toString output.
     */
    override fun toString(): String {
        return "AuthorisationCode(value='REDACTED', issuedAt=$issuedAt, expiresAt=$expiresAt, clientId=$clientId, username=$username, redirectUri='$redirectUri', requestedScope=$requestedScope)"
    }

    /**
     * Generated based on its database ID field [value].
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AuthorisationCode

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
