package uk.co.baconi.oauth.api.serializers

import io.ktor.http.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind.STRING
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class UrlSerializer : KSerializer<Url> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Url", STRING)
    override fun serialize(encoder: Encoder, value: Url) = encoder.encodeString(value.toString())
    override fun deserialize(decoder: Decoder): Url = Url(decoder.decodeString())
}
