package com.sbgcore.oauth.api.openid.introspection

import com.sbgcore.oauth.api.authentication.AuthenticatedClientPrincipal
import com.sbgcore.oauth.api.openid.TokenType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IntrospectionRequest(
    val token: String?,
    @SerialName("token_type_hint") val hint: String?
)

sealed class ValidatedIntrospectionRequest {
    abstract val principal: AuthenticatedClientPrincipal
    abstract val token: String
}

data class IntrospectionRequestBasic(
    override val principal: AuthenticatedClientPrincipal,
    override val token: String
) : ValidatedIntrospectionRequest()

data class IntrospectionRequestWithHint(
    override val principal: AuthenticatedClientPrincipal,
    override val token: String,
    val hint: TokenType
) : ValidatedIntrospectionRequest()