package com.sbgcore.oauth.api.openid.introspection

import com.sbgcore.oauth.api.openid.ClientId
import com.sbgcore.oauth.api.openid.Scopes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

sealed class IntrospectionResponse

@Serializable
data class ActiveIntrospectionResponse(
    val active: Boolean = true,
    val scope: Set<Scopes>,
    @SerialName("client_id") val clientId: ClientId,
    val username: String,
    @SerialName("sub") val subject: String,
    @SerialName("exp") val expirationTime: Long,
    @SerialName("iat") val issuedAt: Long,
    @SerialName("nbf") val notBefore: Long
): IntrospectionResponse()

@Serializable
data class InactiveIntrospectionResponse(val active: Boolean = false): IntrospectionResponse()
