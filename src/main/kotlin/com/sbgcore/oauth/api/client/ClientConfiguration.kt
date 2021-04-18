package com.sbgcore.oauth.api.client

import com.sbgcore.oauth.api.openid.Scopes
import io.ktor.http.*

data class ClientConfiguration(
    val id: ClientId,
    val type: ClientType,
    val redirectUrls: Set<Url>,

    // TODO - Verify the spec, is there such a thing as require and optional or is it just scopes....
    val requiredScopes : Set<Scopes>,
    val optionalScopes : Set<Scopes>
) {
    val isConfidential = type == ClientType.Confidential
    val isPublic = type == ClientType.Public

    init {
        require(requiredScopes.none(optionalScopes::contains)) { "required scopes cannot contain optional scopes" }
        require(optionalScopes.none(requiredScopes::contains)) { "optional scopes cannot contain required scopes" }
    }
}
