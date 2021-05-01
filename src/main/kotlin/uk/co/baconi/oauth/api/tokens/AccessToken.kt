@file:UseSerializers(UUIDSerializer::class, OffsetDateTimeSerializer::class)

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

@Serializable
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

    fun hasExpired(): Boolean = now().isAfter(expiresAt)

    override fun toString(): String {
        return "AccessToken(id=$id, value=REDACTED, username='$username', clientId=$clientId, scopes=$scopes, issuedAt=$issuedAt, expiresAt=$expiresAt, notBefore=$notBefore)"
    }
}
