package com.sbgcore.oauth.api.ktor.auth.bearer

import com.sbgcore.oauth.api.openid.Scopes
import io.ktor.auth.*

/**
 * Represents an OAuth2 Bearer [token] and required [scopes]
 * @property token
 * @property scopes
 */
data class OAuth2BearerCredential(val token: String, val scopes: Set<Scopes>) : Credential