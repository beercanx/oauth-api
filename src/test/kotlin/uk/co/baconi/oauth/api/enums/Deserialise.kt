package uk.co.baconi.oauth.api.enums

import uk.co.baconi.oauth.api.enums.EnumJson.json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.serializer

inline fun <reified A : Enum<A>> String.deserialise(): A {
    return json.decodeFromJsonElement(serializer(), JsonPrimitive(this))
}
