package com.sbgcore.oauth.api.serializers

import com.sbgcore.oauth.api.enums.enumByValue
import com.sbgcore.oauth.api.openid.Scopes
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind.STRING
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Custom scope field serializer because OAuth spec requires it to be a space separated string field.
 */
class ScopeSerializer(@Suppress("UNUSED_PARAMETER") serializer: KSerializer<Scopes>) : KSerializer<Set<Scopes>> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("com.sbgcore.oauth.api.openid.Scope", STRING)

    override fun serialize(encoder: Encoder, value: Set<Scopes>) {
        encoder.encodeString(value.joinToString(separator = " ", transform = Scopes::value))
    }

    override fun deserialize(decoder: Decoder): Set<Scopes> {
        return decoder.decodeString().split(" ").mapNotNull<String, Scopes>(::enumByValue).toSet()
    }
}