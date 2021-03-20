package com.sbgcore.oauth.api.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind.LONG
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Duration

class DurationInMillisecondsSerializer : KSerializer<Duration> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("java.time.Duration", LONG)

    /**
     * Returns a long containing the duration as milliseconds.
     */
    override fun serialize(encoder: Encoder, value: Duration) {
        encoder.encodeLong(value.toMillis())
    }

    /**
     * Expects a long containing a duration in milliseconds.
     */
    override fun deserialize(decoder: Decoder): Duration {
        return Duration.ofMillis(decoder.decodeLong())
    }
}