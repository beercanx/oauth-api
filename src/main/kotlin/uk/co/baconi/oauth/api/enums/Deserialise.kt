package uk.co.baconi.oauth.api.enums

import kotlinx.serialization.serializer
import uk.co.baconi.oauth.api.enums.EnumSerialisation.Companion.INSTANCE

/**
 * Converts a [String] to an enum of type [A] based on its Json like value, as provided by kotlinx.serialization
 */
inline fun <reified A : Enum<A>> String.deserialise(): A? = INSTANCE.deserialise(serializer<A>(), this)
