package com.sbgcore.oauth.api.openid.introspection

import com.sbgcore.oauth.api.openid.exchange.tokens.TokenTypes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RawIntrospectionRequest(
    val token: String?,
    @SerialName("token_type_hint") val hint: TokenTypes?
)
