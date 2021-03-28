package com.sbgcore.oauth.api.client

import io.ktor.http.*

data class ClientConfiguration(
    val id: ClientId,
    val type: ClientType,
    val redirectUrls: Set<Url>
    // TODO - requiredScopes
    // TODO - optionalScopes
) {
    val isConfidential = type == ClientType.Confidential
    val isPublic = type == ClientType.Public
}
