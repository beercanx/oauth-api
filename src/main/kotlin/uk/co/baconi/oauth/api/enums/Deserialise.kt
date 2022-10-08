package uk.co.baconi.oauth.api.enums

import uk.co.baconi.oauth.api.enums.EnumJson.json
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.serializer

/**
 * Converts a [String] to an [A] based on its Json like value, as provided by kotlinx.serialization
 */
inline fun <reified A : Enum<A>> deserialise(string: String): A? = deserialise(serializer<A>(), string)

/**
 * Converts a [String] to an [A] based on its Json like value, as provided by kotlinx.serialization
 */
fun <A : Enum<A>> deserialise(deserializer: DeserializationStrategy<A>, string: String): A? = try {
    json.decodeFromJsonElement(deserializer, JsonPrimitive(string))
} catch (exception: Exception) {
    null
}
