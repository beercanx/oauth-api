package com.sbgcore.oauth.api.client

import com.sbgcore.oauth.api.openid.Scopes
import io.ktor.http.*

data class ClientConfiguration(
    val id: ClientId,
    val type: ClientType,
    val redirectUrls: Set<Url>,
    val allowedScopes : Set<Scopes>
) {
    val isConfidential = type == ClientType.Confidential
    val isPublic = type == ClientType.Public
}
