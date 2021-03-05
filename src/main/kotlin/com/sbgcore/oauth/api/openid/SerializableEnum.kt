package com.sbgcore.oauth.api.openid

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.Json

interface SerializableEnum {

    private val format
        get() = Json {
            encodeDefaults = true
        }

    /**
     * Help extract the name from the [SerialName] annotation.
     *
     * @return the non null serial name of the enum.
     * @throws IllegalStateException if there is no [SerialName] annotation found.
     */
    fun <T : Any> T.getSerialName(serializer: KSerializer<T>): String {
        return format.encodeToString(serializer, this)
    }
}