package com.sbgcore.oauth.api.openid

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Scopes(val value: String) {
    @SerialName("openid") OpenId("openid")
}