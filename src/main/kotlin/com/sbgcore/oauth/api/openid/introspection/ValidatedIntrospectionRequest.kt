package com.sbgcore.oauth.api.openid.introspection

import com.sbgcore.oauth.api.authentication.ConfidentialClient
import com.sbgcore.oauth.api.openid.exchange.tokens.TokenTypes

sealed class ValidatedIntrospectionRequest {
    abstract val principal: ConfidentialClient
    abstract val token: String
}

data class IntrospectionRequest(
    override val principal: ConfidentialClient,
    override val token: String
) : ValidatedIntrospectionRequest()

data class IntrospectionRequestWithHint(
    override val principal: ConfidentialClient,
    override val token: String,
    val hint: TokenTypes
) : ValidatedIntrospectionRequest()
