package uk.co.baconi.oauth.api.enums

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull

class EnumSerialisation internal constructor(private val json: Json) {

    private constructor() : this(Json {})

    companion object {
        val INSTANCE = EnumSerialisation()
    }

    /**
     * Converts an [A] to a [String] based on its Json like value, as provided by kotlinx.serialization
     */
    fun <A : Enum<A>> serialise(serializer: SerializationStrategy<A>, enum: A): String? = try {
        (json.encodeToJsonElement(serializer, enum) as? JsonPrimitive)?.contentOrNull
    } catch (exception: Exception) {
        null
    }

    /**
     * Converts a [String] to an [A] based on its Json like value, as provided by kotlinx.serialization
     */
    fun <A : Enum<A>> deserialise(deserializer: DeserializationStrategy<A>, string: String): A? = try {
        json.decodeFromJsonElement(deserializer, JsonPrimitive(string))
    } catch (exception: Exception) {
        null
    }
}
