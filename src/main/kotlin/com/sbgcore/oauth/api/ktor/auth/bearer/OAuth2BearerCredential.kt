package com.sbgcore.oauth.api.ktor.auth.bearer

import io.ktor.auth.*

/**
 * Represents an OAuth2 Bearer [token]
 * @property token
 */
data class OAuth2BearerCredential(val token: String) : Credential