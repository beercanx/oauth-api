package uk.co.baconi.session

//import kotlinx.datetime.*
//import kotlinx.datetime.Clock.System.now
//import kotlinx.datetime.DateTimeUnit.Companion.MINUTE
//import kotlinx.serialization.KSerializer
//import kotlinx.serialization.descriptors.PrimitiveKind
//import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
//import kotlinx.serialization.descriptors.SerialDescriptor
//import kotlinx.serialization.encoding.Decoder
//import kotlinx.serialization.encoding.Encoder
//
//object ExpiresInSerializer : KSerializer<Instant> {
//    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("SpaceDelimited", PrimitiveKind.STRING)
//    override fun deserialize(decoder: Decoder): Instant {
//        return now().plus(decoder.decodeLong(), MINUTE)
//    }
//    override fun serialize(encoder: Encoder, value: Instant) {
//        encoder.encodeLong(now().until(value, MINUTE))
//    }
//}