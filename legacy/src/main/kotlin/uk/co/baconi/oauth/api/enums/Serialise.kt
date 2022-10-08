package uk.co.baconi.oauth.api.enums

import kotlinx.serialization.serializer
import uk.co.baconi.oauth.api.enums.EnumSerialisation.Companion.INSTANCE

/**
 * Converts an [A] to a [String] based on its Json like value, as provided by kotlinx.serialization
 */
inline fun <reified A : Enum<A>> A.serialise(): String? = INSTANCE.serialise(serializer(), this)
