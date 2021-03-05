package com.sbgcore.oauth.api.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind.STRING
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.OffsetDateTime

class OffsetDateTimeSerializer : KSerializer<OffsetDateTime> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("java.time.OffsetDateTime", STRING)

    /**
     * Returns a string in the ISO_OFFSET_DATE_TIME format, e.g. 2011-12-03T10:15:30+01:00
     */
    override fun serialize(encoder: Encoder, value: OffsetDateTime) {
        encoder.encodeString(value.toString())
    }

    /**
     * Expects a string in the ISO_OFFSET_DATE_TIME format, e.g. 2011-12-03T10:15:30+01:00
     */
    override fun deserialize(decoder: Decoder): OffsetDateTime {
        return OffsetDateTime.parse(decoder.decodeString())
    }
}