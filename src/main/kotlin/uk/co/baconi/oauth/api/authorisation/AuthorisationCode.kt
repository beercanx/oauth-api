package uk.co.baconi.oauth.api.authorisation

import org.dizitart.no2.objects.Id
import uk.co.baconi.oauth.api.client.ClientId
import uk.co.baconi.oauth.api.authentication.AuthenticatedUsername
import uk.co.baconi.oauth.api.scopes.Scopes
import java.time.OffsetDateTime

// TODO - Expand with consumer issued to details
data class AuthorisationCode(

    // The actual code
    @Id val value: String,

    // Used to calculate when it expires?
    val issuedAt: OffsetDateTime,

    // Issued to
    val clientId: ClientId,

    // Issued for
    val username: AuthenticatedUsername,

    // Added because we need to validate on exchange its the same url as stated in https://tools.ietf.org/html/rfc6749#section-4.1.3
    val redirectUri: String,

    // The requested scopes
    val requestedScope: Set<Scopes>

) {

    /**
     * Generated to exclude [value] from the toString output.
     */
    override fun toString(): String {
        return "AuthorisationCode(value='REDACTED', issuedAt=$issuedAt, clientId=$clientId, username='$username', redirectUri='$redirectUri')"
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
