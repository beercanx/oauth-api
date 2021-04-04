package com.sbgcore.oauth.api.openid.introspection

import com.sbgcore.oauth.api.client.ConfidentialClient
import com.sbgcore.oauth.api.tokens.Tokens
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RawIntrospectionRequest(
    val token: String?,
    @SerialName("token_type_hint") val hint: Tokens? = null
)

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
    val hint: Tokens
) : ValidatedIntrospectionRequest()
