package com.sbgcore.oauth.api

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive

@Deprecated("Should be possible to replace usage of this by better kotlinx.serialisation integration, right?")
interface SerializableEnum {

    private val format
        get() = Json {
            encodeDefaults = true
        }

    val value: String

    /**
     * Help extract the name from the [SerialName] annotation.
     *
     * @return the non null serial name of the enum.
     * @throws IllegalStateException if there is no [SerialName] annotation found.
     */
    fun <T : Any> T.getSerialName(serializer: KSerializer<T>): String {
        return (format.encodeToJsonElement(serializer, this) as JsonPrimitive).content
    }
}

/**
 * Find a [SerializableEnum] by its JSON value.
 */
@Deprecated("Should be possible to replace usage of this by better kotlinx.serialisation integration, right?")
inline fun <reified A> enumByValue(value: String): A? where A : Enum<A>, A : SerializableEnum {
    return enumValues<A>().find { a -> a.value == value }
}