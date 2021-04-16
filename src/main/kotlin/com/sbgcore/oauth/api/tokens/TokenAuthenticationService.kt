package com.sbgcore.oauth.api.tokens

import com.sbgcore.oauth.api.openid.Scopes
import java.util.*

class TokenAuthenticationService(private val repository: AccessTokenRepository) {

    fun accessTokenWithScopes(token: String, requiredScopes: Set<Scopes>): AccessToken? {
        val accessToken = repository.findByValue(token)
        return when {
            accessToken == null -> null
            accessToken.scopes.containsAll(requiredScopes) -> accessToken
            else -> null
        }
    }
}