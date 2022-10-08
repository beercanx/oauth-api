package uk.co.baconi.oauth.api.common.scope

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object ScopeSerializer : KSerializer<Scope> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Scope", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Scope) {
        encoder.encodeString(value.value)
    }

    // TODO - Introduce [Scope] validation
    override fun deserialize(decoder: Decoder): Scope {
        return Scope(decoder.decodeString())
    }
}