package uk.co.baconi.oauth.api.tokens

import uk.co.baconi.oauth.api.client.ClientId
import uk.co.baconi.oauth.api.openid.Scopes
import uk.co.baconi.oauth.api.serializers.OffsetDateTimeSerializer
import uk.co.baconi.oauth.api.serializers.UUIDSerializer
import io.ktor.auth.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.dizitart.no2.objects.Id
import java.time.OffsetDateTime
import java.time.OffsetDateTime.now
import java.util.*

data class AccessToken(
    @Id override val id: UUID,
    override val value: UUID, // TODO - Replace by a secure random hash so it doesn't look like an ID?
    override val username: String,
    override val clientId: ClientId,
    override val scopes: Set<Scopes>,
    override val issuedAt: OffsetDateTime,  // TODO - Does OffsetDateTimeSerializer return the right format?
    override val expiresAt: OffsetDateTime, // TODO - Does OffsetDateTimeSerializer return the right format?
    override val notBefore: OffsetDateTime  // TODO - Does OffsetDateTimeSerializer return the right format?
) : Token, Principal {

    companion object

    fun hasExpired(): Boolean = now().isAfter(expiresAt)

    override fun toString(): String {
        return "AccessToken(id=$id, value=REDACTED, username='$username', clientId=$clientId, scopes=$scopes, issuedAt=$issuedAt, expiresAt=$expiresAt, notBefore=$notBefore)"
    }

    /**
     * Custom generated because of :
     *  - How the database serialises the date objects
     *  - OffsetDateTime.equals returns false when comparing 11:00+0000 to 12:00+0100
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AccessToken

        if (id != other.id) return false
        if (value != other.value) return false
        if (username != other.username) return false
        if (clientId != other.clientId) return false
        if (scopes != other.scopes) return false
        if (issuedAt isNotEqual other.issuedAt) return false
        if (expiresAt isNotEqual other.expiresAt) return false
        if (notBefore isNotEqual other.notBefore) return false

        return true
    }

    /**
     * Generated because when you generate [equals] you have to generated [hashCode].
     */
    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + value.hashCode()
        result = 31 * result + username.hashCode()
        result = 31 * result + clientId.hashCode()
        result = 31 * result + scopes.hashCode()
        result = 31 * result + issuedAt.hashCode()
        result = 31 * result + expiresAt.hashCode()
        result = 31 * result + notBefore.hashCode()
        return result
    }

    /**
     * Lazy helper for checking if an [OffsetDateTime] is temporally equal
     */
    private infix fun OffsetDateTime.isNotEqual(other: OffsetDateTime): Boolean = !isEqual(other)
}
