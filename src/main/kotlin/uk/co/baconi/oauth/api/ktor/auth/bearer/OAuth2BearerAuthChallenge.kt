package uk.co.baconi.oauth.api.ktor.auth.bearer

import uk.co.baconi.oauth.api.scopes.Scopes
import uk.co.baconi.oauth.api.serializers.ScopeSerializer
import io.ktor.http.auth.*
import io.ktor.http.auth.HttpAuthHeader.*

/**
 * Bearer Authentication described in the RFC-6750
 *
 * see https://tools.ietf.org/html/rfc6750
 */
const val Bearer: String = "Bearer"

private val scopeSerializer: ScopeSerializer
    get() = ScopeSerializer()

/**
 * Generates an OAuth2 [Bearer] challenge as a [HttpAuthHeader].
 */
fun oAuth2BearerAuthChallenge(realm: String?, scopes: Set<Scopes>? = null): Parameterized = Parameterized(
    Bearer,
    LinkedHashMap<String, String>().apply {
        if (realm != null) {
            put(Parameters.Realm, realm)
        }
        if (scopes != null) {
            put(Parameters.Realm, scopeSerializer.serialize(scopes))
        }
    }
)