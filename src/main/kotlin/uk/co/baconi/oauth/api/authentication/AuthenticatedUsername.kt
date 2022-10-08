package uk.co.baconi.oauth.api.authentication

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = UsernameSerializer::class)
data class AuthenticatedUsername(val value: String) {
    override fun toString(): String = value
}

class UsernameSerializer : KSerializer<AuthenticatedUsername> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("AuthenticatedUsername", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: AuthenticatedUsername) = encoder.encodeString(value.value)
    override fun deserialize(decoder: Decoder): AuthenticatedUsername = AuthenticatedUsername(decoder.decodeString())
}