@file:UseSerializers(UUIDSerializer::class, OffsetDateTimeSerializer::class)

package com.sbgcore.oauth.api.tokens

import com.sbgcore.oauth.api.client.ClientId
import com.sbgcore.oauth.api.openid.Scopes
import com.sbgcore.oauth.api.serializers.OffsetDateTimeSerializer
import com.sbgcore.oauth.api.serializers.UUIDSerializer
import io.ktor.auth.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.dizitart.no2.objects.Id
import java.time.OffsetDateTime
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
    override fun toString(): String {
        return "AccessToken(id=$id, value=REDACTED, username='$username', clientId=$clientId, scopes=$scopes, issuedAt=$issuedAt, expiresAt=$expiresAt, notBefore=$notBefore)"
    }
}
