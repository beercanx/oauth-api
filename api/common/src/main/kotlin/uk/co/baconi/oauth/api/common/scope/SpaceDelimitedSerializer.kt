package uk.co.baconi.oauth.api.common.scope

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

abstract class SpaceDelimitedSerializer<T> : KSerializer<Set<T>> {

    protected abstract fun encode(value: T): String
    protected abstract fun decode(string: String): T?

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("SpaceDelimited", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): Set<T> = deserialize(decoder.decodeString())
    override fun serialize(encoder: Encoder, value: Set<T>) = encoder.encodeString(serialize(value))

    fun deserialize(string: String): Set<T> = string.split(" ").mapNotNull(::decode).toSet()
    fun serialize(value: Set<T>): String = value.joinToString(separator = " ", transform = ::encode)

}