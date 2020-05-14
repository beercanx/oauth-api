package com.sbgcore.oauth.api.openid

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class TokenType {
    @SerialName("access_token") AccessToken,
    @SerialName("refresh_token") RefreshToken
}