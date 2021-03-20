package com.sbgcore.oauth.api.openid.exchange.tokens

import com.sbgcore.oauth.api.openid.SerializableEnum
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