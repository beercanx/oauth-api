/*
* Copyright 2014-2021 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
*/
package uk.co.baconi.oauth.api.ktor.auth.bearer

import io.ktor.http.auth.*
import io.ktor.http.auth.HttpAuthHeader.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import uk.co.baconi.oauth.api.ktor.auth.OAuth21ResponseParameters
import uk.co.baconi.oauth.api.ktor.auth.bearer.BearerAuthenticationProvider.Config
import uk.co.baconi.oauth.api.scopes.ScopeSerializer
import uk.co.baconi.oauth.api.scopes.Scopes

/**
 * A `bearer` [Authentication] provider.
 *
 * @see [bearer]
 * @property name is the name of the provider, or `null` for a default provider.
 */
class BearerAuthenticationProvider internal constructor(config: Config) : AuthenticationProvider(config) {

    internal val realm: String = config.realm

    internal val authenticationFunction = config.authenticationFunction

    override suspend fun onAuthenticate(context: AuthenticationContext) {
        val call = context.call
        val credentials = call.request.bearerAuthenticationCredentials()
        val principal = credentials?.let { authenticationFunction(call, it) }

        val cause = when {
            credentials == null -> AuthenticationFailedCause.NoCredentials
            principal == null -> AuthenticationFailedCause.InvalidCredentials
            else -> null
        }

        if (cause != null) {
            context.challenge(bearerAuthenticationChallengeKey, cause) { challenge, challengeCall ->
                challengeCall.respond(UnauthorizedResponse(bearerAuthChallenge(realm, null, null)))
                challenge.complete()
            }
        }

        if (principal != null) {
            context.principal(principal)
        }
    }

    /**
     * A configuration for the [bearer] authentication provider.
     */
    class Config internal constructor(name: String?) : AuthenticationProvider.Config(name) {

        internal var authenticationFunction: AuthenticationFunction<BearerCredential> = {
            throw NotImplementedError(
                "Bearer auth validate function is not specified. Use bearer { validate { ... } } to fix."
            )
        }

        /**
         * Specifies a realm to be passed in the `WWW-Authenticate` header.
         */
        var realm: String = "Ktor Server"

        /**
         * Sets a validation function that checks a specified [BearerCredential] instance and
         * returns [Principal] in a case of successful authentication or null if authentication fails.
         */
        fun validate(body: suspend ApplicationCall.(BearerCredential) -> Principal?) {
            authenticationFunction = body
        }
    }
}

/**
 * Installs the bearer [Authentication] provider.
 * You can use bearer authentication for logging in users and protecting specific routes.
 * To learn how to configure it, see [Bearer authentication](https://ktor.io/docs/bearer.html).
 */
fun AuthenticationConfig.bearer(name: String? = null, configure: Config.() -> Unit) {
    val provider = BearerAuthenticationProvider(Config(name).apply(configure))
    register(provider)
}

/**
 * Represents a Bearer [token]
 * @see [Authentication]
 * @property token
 */
data class BearerCredential(val token: String) : Credential

/**
 * Retrieves [bearer] authentication credentials for this [ApplicationRequest].
 */
private fun ApplicationRequest.bearerAuthenticationCredentials(): BearerCredential? {
    when (val authHeader = parseAuthorizationHeader()) {
        is Single -> {
            // Verify the auth scheme is HTTP Bearer. According to RFC 2617, the authorization scheme should not be case
            // sensitive; thus BEARER, or Bearer, or bearer are all valid.
            if (!authHeader.authScheme.equals(AuthScheme.Bearer, ignoreCase = true)) {
                return null
            }

            return BearerCredential(authHeader.blob)
        }
        else -> return null
    }
}

private val bearerAuthenticationChallengeKey: Any = "BearerAuth"

private val scopeSerializer: ScopeSerializer
    get() = ScopeSerializer()

/**
 * Generates a Bearer challenge as a [HttpAuthHeader].
 */
fun bearerAuthChallenge(
    realm: String,
    error: BearerErrorCode? = null,
    errorDescription: String? = null,
    scopes: Set<Scopes>? = null
) = Parameterized(
    AuthScheme.Bearer,
    buildMap {
        put(Parameters.Realm, realm)
        if (error != null) {
            put(OAuth2ResponseParameters.Error, error.value)
        }
        if (errorDescription != null) {
            put(OAuth2ResponseParameters.ErrorDescription, errorDescription)
        }
        if (scopes != null) {
            // https://www.ietf.org/archive/id/draft-parecki-oauth-v2-1-01.html#name-the-www-authenticate-respon
            put(OAuth21ResponseParameters.Scope, scopeSerializer.serialize(scopes))
        }
    }
)