package uk.co.baconi.oauth.api.serializers

import uk.co.baconi.oauth.api.enums.deserialise
import uk.co.baconi.oauth.api.enums.serialise
import uk.co.baconi.oauth.api.openid.Scopes
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

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("uk.co.baconi.oauth.api.openid.Scope", STRING)

    override fun serialize(encoder: Encoder, value: Set<Scopes>) {
        encoder.encodeString(serialize(value))
    }

    override fun deserialize(decoder: Decoder): Set<Scopes> {
        return deserialize(decoder.decodeString())
    }

    fun serialize(data: Set<Scopes>): String = data.joinToString(separator = " ") { scope ->
        serialise(scopesSerializer, scope) ?: EMPTY
    }

    fun deserialize(data: String): Set<Scopes> = data.split(" ").mapNotNull { scope ->
        deserialise(scopesSerializer, scope)
    }.toSet()

}