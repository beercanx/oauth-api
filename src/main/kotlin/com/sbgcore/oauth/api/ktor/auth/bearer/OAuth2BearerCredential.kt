package com.sbgcore.oauth.api.ktor.auth.bearer

import com.sbgcore.oauth.api.openid.Scopes
import io.ktor.auth.*

/**
 * Represents an OAuth2 Bearer [token] and [requiredScopes]
 * @property token
 * @property requiredScopes
 */
data class OAuth2BearerCredential(val token: String, val requiredScopes: Set<Scopes>) : Credential