package uk.co.baconi.oauth.api.common.scope

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

// Created to prevent accidental deserialisation of a Scope from external input.
object ScopeSerializer : KSerializer<Scope> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Scope", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: Scope) = encoder.encodeString(value.value)
    override fun deserialize(decoder: Decoder): Scope = throw SerializationException("Deserialize unsupported for Scope")
}