package com.sbgcore.oauth.api.ktor.auth.bearer

import io.ktor.http.auth.*
import io.ktor.http.auth.HttpAuthHeader.*

/**
 * Bearer Authentication described in the RFC-6750
 *
 * see https://tools.ietf.org/html/rfc6750
 */
const val Bearer: String = "Bearer"

/**
 * Generates an OAuth2 [Bearer] challenge as a [HttpAuthHeader].
 */
fun oAuth2BearerAuthChallenge(realm: String?): Parameterized = Parameterized(
    Bearer,
    LinkedHashMap<String, String>().apply {
        if (realm != null) {
            put(Parameters.Realm, realm)
        }
    }
)