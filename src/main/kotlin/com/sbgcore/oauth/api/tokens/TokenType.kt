package com.sbgcore.oauth.api.tokens

import com.sbgcore.oauth.api.SerializableEnum
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class TokenType : SerializableEnum {

    @SerialName("bearer") Bearer,

    ;

    override val value: String by lazy {
        getSerialName(serializer())
    }
}