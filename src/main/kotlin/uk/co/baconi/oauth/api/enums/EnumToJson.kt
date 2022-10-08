package uk.co.baconi.oauth.api.enums

import uk.co.baconi.oauth.api.enums.EnumJson.json
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.serializer

/**
 * Converts an [A] to a [String] based on its Json like value, as provided by kotlinx.serialization
 */
inline fun <reified A : Enum<A>> enumToJson(enum: A): String? = enumToJson(serializer(), enum)

/**
 * Converts an [A] to a [String] based on its Json like value, as provided by kotlinx.serialization
 */
fun <A : Enum<A>> enumToJson(serializer: SerializationStrategy<A>, enum: A): String? = try {
    (json.encodeToJsonElement(serializer, enum) as JsonPrimitive).content
} catch (exception: Exception) {
    // TODO - Logging?
    null
}