package uk.co.baconi.oauth.common.authentication

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Supports only deserialisation for use with HTTP clients getting a response from the Kotlin/JVM instance.
 */
actual object AuthenticatedUsernameSerializer : KSerializer<AuthenticatedUsername> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("AuthenticatedUsername", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: AuthenticatedUsername) {
        throw SerializationException("Serialize unsupported for AuthenticatedUsername in Kotlin/JS")
    }

    override fun deserialize(decoder: Decoder): AuthenticatedUsername {
        val value = decoder.decodeString()
        return when {
            value.isBlank() -> throw SerializationException("Invalid authenticated username [${value}]")
            value.trim() != value -> throw SerializationException("Invalid authenticated username [${value}]")
            else -> AuthenticatedUsername(value)
        }
    }
}