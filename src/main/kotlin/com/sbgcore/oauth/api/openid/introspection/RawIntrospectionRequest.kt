package com.sbgcore.oauth.api.openid.introspection

import com.sbgcore.oauth.api.authentication.AuthenticatedClient
import com.sbgcore.oauth.api.openid.TokenType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RawIntrospectionRequest(
    val token: String?,
    @SerialName("token_type_hint") val hint: String?
)

sealed class ValidatedIntrospectionRequest {
    abstract val principal: AuthenticatedClient
    abstract val token: String
}

data class IntrospectionRequest(
    override val principal: AuthenticatedClient,
    override val token: String
) : ValidatedIntrospectionRequest()

data class IntrospectionRequestWithHint(
    override val principal: AuthenticatedClient,
    override val token: String,
    val hint: TokenType
) : ValidatedIntrospectionRequest()