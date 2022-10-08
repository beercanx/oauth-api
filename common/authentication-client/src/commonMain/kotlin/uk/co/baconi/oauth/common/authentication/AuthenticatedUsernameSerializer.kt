package uk.co.baconi.oauth.common.authentication

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object AuthenticatedUsernameSerializer : KSerializer<AuthenticatedUsername> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("AuthenticatedUsername", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: AuthenticatedUsername) = encoder.encodeString(value.value)
    override fun deserialize(decoder: Decoder): AuthenticatedUsername = AuthenticatedUsername(decoder.decodeString())
}
