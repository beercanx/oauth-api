package com.sbgcore.oauth.api.tokens

import java.time.OffsetDateTime.now

class TokenAuthenticationService(private val repository: AccessTokenRepository) {

    fun accessToken(token: String): AccessToken? {
        val accessToken = repository.findByValue(token)
        return when {
            accessToken == null -> null
            now().isAfter(accessToken.expiresAt) -> null
            else -> accessToken
        }
    }
}