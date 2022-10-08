package uk.co.baconi.oauth.api.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind.STRING
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import uk.co.baconi.oauth.api.enums.EnumSerialisation.Companion.INSTANCE
import uk.co.baconi.oauth.api.openid.Scopes

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
        INSTANCE.serialise(scopesSerializer, scope) ?: EMPTY
    }

    fun deserialize(data: String): Set<Scopes> = data.split(" ").mapNotNull { scope ->
        INSTANCE.deserialise(scopesSerializer, scope)
    }.toSet()

}