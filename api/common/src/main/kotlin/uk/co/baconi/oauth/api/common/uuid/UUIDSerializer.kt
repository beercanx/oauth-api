package uk.co.baconi.oauth.api.common.uuid

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.*

object UUIDSerializer : KSerializer<UUID> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: UUID) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): UUID {
        return try {
            UUID.fromString(decoder.decodeString())
        } catch (cause: Exception) {
            throw SerializationException(cause)
        }
    }

    fun fromValueOrNull(value: String?): UUID? {
        if(value == null) return null
        return try {
            UUID.fromString(value)
        } catch (cause: Exception) {
            null
        }
    }
}