package com.sbgcore.oauth.api.openid.exchange.tokens

import com.sbgcore.oauth.api.openid.SerializableEnum
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class TokenTypes : SerializableEnum {
    @SerialName("access_token") AccessToken,
    //@SerialName("refresh_token") RefreshToken // TODO - Add back in once we've implemented refresh flow.
    ;

    override val value: String by lazy {
        getSerialName(serializer())
    }
}