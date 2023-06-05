package uk.co.baconi.oauth.api.common.client

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object ClientIdSerializer : KSerializer<ClientId> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ClientId", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ClientId) {
        when {
            value.value.isBlank() -> throw SerializationException("Invalid client id [${value.value}]")
            value.value.trim() != value.value ->  throw SerializationException("Invalid client id [${value.value}]")
            else -> encoder.encodeString(value.value)
        }
    }

    override fun deserialize(decoder: Decoder): ClientId {
        throw SerializationException("Deserialize unsupported for ClientId")
    }
}