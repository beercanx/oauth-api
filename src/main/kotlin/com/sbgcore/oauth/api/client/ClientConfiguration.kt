package com.sbgcore.oauth.api.client

data class ClientConfiguration(val id: ClientId, val type: ClientType) {
    val isConfidential = type == ClientType.Confidential
    val isPublic = type == ClientType.Public
}
