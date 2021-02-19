package com.sbgcore.oauth.api.openid

import kotlinx.serialization.SerialName
import kotlin.reflect.full.findAnnotation

interface SerializableEnum {

    /**
     * Help extract the name from the [SerialName] annotation.
     *
     * @return the non null serial name of the enum.
     * @throws IllegalStateException if there is no [SerialName] annotation found.
     */
    fun getSerialName(): String {
        return checkNotNull(this::class.findAnnotation<SerialName>()?.value) {
            "Unable to find SerialName for: ${this::class}.$this"
        }
    }
}