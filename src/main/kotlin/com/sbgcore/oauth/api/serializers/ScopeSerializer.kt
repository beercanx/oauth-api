package com.sbgcore.oauth.api.serializers

import com.sbgcore.oauth.api.enums.enumByJson
import com.sbgcore.oauth.api.enums.enumToJson
import com.sbgcore.oauth.api.openid.Scopes
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind.STRING
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Custom scope field serializer because OAuth spec requires it to be a space separated string field.
 * For example it will look like: `openid profile::read`
 */
class ScopeSerializer(private val scopesSerializer: KSerializer<Scopes>) : KSerializer<Set<Scopes>> {

    constructor() : this(Scopes.serializer())

    companion object {
        private const val EMPTY = ""
    }

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("com.sbgcore.oauth.api.openid.Scope", STRING)

    override fun serialize(encoder: Encoder, value: Set<Scopes>) {
        encoder.encodeString(serialize(value))
    }

    override fun deserialize(decoder: Decoder): Set<Scopes> {
        return deserialize(decoder.decodeString())
    }

    fun serialize(data: Set<Scopes>): String = data.joinToString(separator = " ") { scope ->
        enumToJson(scopesSerializer, scope) ?: EMPTY
    }

    fun deserialize(data: String): Set<Scopes> = data.split(" ").mapNotNull { scope ->
        enumByJson(scopesSerializer, scope)
    }.toSet()

}