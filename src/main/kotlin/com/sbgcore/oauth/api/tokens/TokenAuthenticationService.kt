package com.sbgcore.oauth.api.tokens

class TokenAuthenticationService(private val repository: AccessTokenRepository) {

    fun accessToken(token: String): AccessToken? {
        val accessToken = repository.findByValue(token)
        return when {
            accessToken == null -> null
            accessToken.hasExpired() -> null // Unlikely but possible
            else -> accessToken
        }
    }
}