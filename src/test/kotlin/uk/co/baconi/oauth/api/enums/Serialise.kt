package uk.co.baconi.oauth.api.enums

import uk.co.baconi.oauth.api.enums.EnumJson.json
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.serializer
import kotlinx.serialization.SerializationException

inline fun <reified A : Enum<A>> A.serialise(): String {
    return (json.encodeToJsonElement(serializer(), this) as JsonPrimitive).content
}
