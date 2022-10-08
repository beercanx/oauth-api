package uk.co.baconi.oauth.api.common.scope

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind.STRING
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Custom scope field serializer because OAuth spec requires it to be a space separated string field.
 * For example, it will look like: `openid profile::read`
 */
object ScopesSerializer : KSerializer<Set<Scope>> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Scopes", STRING)

    override fun serialize(encoder: Encoder, value: Set<Scope>) {
        encoder.encodeString(value.joinToString(separator = " ", transform = Scope::value))
    }

    // TODO - Introduce [Scope] validation
    override fun deserialize(decoder: Decoder): Set<Scope> {
        return decoder.decodeString().split(" ").map(::Scope).toSet()
    }
}