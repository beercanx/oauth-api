package com.sbgcore.oauth.api.serializers

import com.sbgcore.oauth.api.openid.Scopes
import com.sbgcore.oauth.api.openid.enumByValue
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class ScopeSerializer(serializer: KSerializer<Scopes>) : KSerializer<Set<Scopes>> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("com.sbgcore.oauth.api.openid.Scope", PrimitiveKind.STRING)

    // TODO - Work out how to use a sub serializer

    override fun serialize(encoder: Encoder, value: Set<Scopes>) {
        encoder.encodeString(value.joinToString(separator = " ", transform = Scopes::value))
    }

    override fun deserialize(decoder: Decoder): Set<Scopes> {
        return decoder.decodeString().split(" ").mapNotNull<String, Scopes>(::enumByValue).toSet()
    }
}