@file:UseSerializers(SessionInfoResponse.InstantSerializer::class)

package uk.co.baconi.oauth.api.session.info

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import uk.co.baconi.oauth.api.common.authentication.AuthenticatedSession
import uk.co.baconi.oauth.api.common.client.ClientId
import uk.co.baconi.oauth.api.common.token.AccessToken
import uk.co.baconi.oauth.api.common.token.RefreshToken
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit.SECONDS

@Serializable
data class SessionInfoResponse(val session: AuthenticatedSession?, val tokens: Tokens?) {

    @Serializable
    data class Tokens(val accessTokens: List<Token>, val refreshTokens: List<Token>)

    @Serializable
    data class Token(val clientId: ClientId, val issuedAt: Instant, val expiresAt: Instant) {
        constructor(token: AccessToken): this(token.clientId, token.issuedAt.truncatedTo(SECONDS), token.expiresAt.truncatedTo(SECONDS))
        constructor(token: RefreshToken): this(token.clientId, token.issuedAt.truncatedTo(SECONDS), token.expiresAt.truncatedTo(SECONDS))
    }

    object InstantSerializer : KSerializer<Instant> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)
        private val formatter = DateTimeFormatter.ISO_INSTANT
        override fun deserialize(decoder: Decoder): Instant = formatter.parse(decoder.decodeString(), Instant::from)
        override fun serialize(encoder: Encoder, value: Instant) = encoder.encodeString(formatter.format(value))
    }
}
