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
        encoder.encodeString(value.value)
    }

    override fun deserialize(decoder: Decoder): ClientId {
        // TODO - Do we even need to support this?
        throw SerializationException("Deserialize unsupported for ClientId")
    }
}