package com.sbgcore.oauth.api.enums

import com.sbgcore.oauth.api.enums.EnumJson.json
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.serializer

/**
 * Converts a [String] to an [A] based on its Json like value, as provided by kotlinx.serialization
 */
inline fun <reified A : Enum<A>> enumByJson(string: String): A? = enumByJson(serializer<A>(), string)

/**
 * Converts a [String] to an [A] based on its Json like value, as provided by kotlinx.serialization
 */
fun <A : Enum<A>> enumByJson(deserializer: DeserializationStrategy<A>, string: String): A? = try {
    json.decodeFromString(deserializer, """"$string"""")
} catch (exception: Exception) {
    // TODO - Logging?
    null
}
