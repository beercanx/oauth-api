package com.sbgcore.oauth.api.serializers

import com.sbgcore.oauth.api.client.ClientId
import com.sbgcore.oauth.api.enums.WithValue
import com.sbgcore.oauth.api.enums.enumByValue
import com.sbgcore.oauth.api.openid.Claims
import com.sbgcore.oauth.api.openid.GrantType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.reflect.KClass

class ClientIdSerializer : EnumWithValueSerializer<ClientId>(ClientId::class) {
    override fun getEnumByValue(value: String): ClientId? = enumByValue(value)
}

class ClaimsSerializer : EnumWithValueSerializer<Claims>(Claims::class) {
    override fun getEnumByValue(value: String): Claims? = enumByValue(value)
}

class GrantTypeSerializer : EnumWithValueSerializer<GrantType>(GrantType::class) {
    override fun getEnumByValue(value: String): GrantType? = enumByValue(value)
}

sealed class EnumWithValueSerializer<E>(
    private val type: KClass<E>
) : KSerializer<E> where E : Enum<E>, E : WithValue {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(type.qualifiedName!!, PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: E) {
        encoder.encodeString(value.value)
    }

    override fun deserialize(decoder: Decoder): E {
        val decoded = decoder.decodeString()
        return when(val value = getEnumByValue(decoded)) {
            null -> throw SerializationException("Unknown enum value [$decoded] for type [$type]") // TODO - Should we be including decoded in this?
            else -> value
        }
    }

    protected abstract fun getEnumByValue(value: String): E?
}