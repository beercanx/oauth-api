package com.sbgcore.oauth.api.ktor.auth.bearer

import com.sbgcore.oauth.api.openid.Scopes
import com.sbgcore.oauth.api.serializers.ScopeSerializer
import io.ktor.http.auth.*
import io.ktor.http.auth.HttpAuthHeader.*

/**
 * Bearer Authentication described in the RFC-6750
 *
 * see https://tools.ietf.org/html/rfc6750
 */
const val Bearer: String = "Bearer"

/**
 * The "scope" attribute is a space-delimited list of case-sensitive scope
 * values indicating the required scope of the access token for
 * accessing the requested resource.
 */
const val Scope: String = "scope"

/**
 * Handles the production of a scope list to OAuth specs.
 */
private val scopeSerializer: ScopeSerializer
    get() = ScopeSerializer()

/**
 * Generates an OAuth2 [Bearer] challenge as a [HttpAuthHeader].
 */
fun oAuth2BearerAuthChallenge(realm: String?, scopes: Set<Scopes>?): Parameterized = Parameterized(
    Bearer,
    LinkedHashMap<String, String>().apply {
        if (realm != null) {
            put(Parameters.Realm, realm)
        }
        if (scopes != null && scopes.isNotEmpty()) {
            put(Scope, scopeSerializer.serialize(scopes))
        }
    }
)