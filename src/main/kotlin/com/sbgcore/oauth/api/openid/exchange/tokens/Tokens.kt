package com.sbgcore.oauth.api.openid.exchange.tokens

import com.sbgcore.oauth.api.openid.SerializableEnum
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Tokens : SerializableEnum {

    @SerialName("access_token") AccessToken,

    ;

    override val value: String by lazy {
        getSerialName(serializer())
    }
}