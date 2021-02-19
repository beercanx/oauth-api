package com.sbgcore.oauth.api.openid.exchange.tokens

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class TokenTypes {
    @SerialName("access_token") AccessToken,
    @SerialName("refresh_token") RefreshToken
}