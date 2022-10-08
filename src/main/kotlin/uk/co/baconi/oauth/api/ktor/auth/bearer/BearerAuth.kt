/*
 * Copyright 2014-2020 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */
package uk.co.baconi.oauth.api.ktor.auth.bearer

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.auth.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.utils.io.charsets.*
import uk.co.baconi.oauth.api.scopes.Scopes
import uk.co.baconi.oauth.api.scopes.ScopeSerializer
import kotlin.text.Charsets

/**
 * Represents a Bearer authentication provider
 * @property name is the name of the provider, or `null` for a default provider
 */
class BearerAuthenticationProvider internal constructor(
    configuration: Configuration
) : AuthenticationProvider(configuration) {

    internal val realm: String = configuration.realm

    internal val authenticationFunction = configuration.authenticationFunction

    /**
     * Basic auth configuration
     */
    class Configuration internal constructor(name: String?) : AuthenticationProvider.Configuration(name) {

        internal var authenticationFunction: AuthenticationFunction<BearerCredential> = {
            throw NotImplementedError(
                "Bearer auth validate function is not specified. Use bearer { validate { ... } } to fix."
            )
        }

        /**
         * Specifies realm to be passed in `WWW-Authenticate` header
         */
        var realm: String = "Ktor Server"

        /**
         * Sets a validation function that will check given [BearerCredential] instance and return [Principal],
         * or null if credential does not correspond to an authenticated principal
         */
        fun validate(body: suspend ApplicationCall.(BearerCredential) -> Principal?) {
            authenticationFunction = body
        }
    }
}

/**
 * Installs a Bearer Authentication mechanism
 */
fun Authentication.Configuration.bearer(
    name: String? = null,
    configure: BearerAuthenticationProvider.Configuration.() -> Unit
) {
    val provider = BearerAuthenticationProvider(BearerAuthenticationProvider.Configuration(name).apply(configure))
    val realm = provider.realm
    val authenticate = provider.authenticationFunction

    provider.pipeline.intercept(AuthenticationPipeline.RequestAuthentication) { context ->
        val credentials = call.request.bearerAuthenticationCredentials()
        val principal = credentials?.let { authenticate(call, it) }

        val cause = when {
            credentials == null -> AuthenticationFailedCause.NoCredentials
            principal == null -> AuthenticationFailedCause.InvalidCredentials
            else -> null
        }

        if (cause != null) {
            context.challenge(bearerAuthenticationChallengeKey, cause) {
                call.respond(UnauthorizedResponse(bearerAuthChallenge(realm)))
                it.complete()
            }
        }
        if (principal != null) {
            context.principal(principal)
        }
    }

    register(provider)
}

private const val bearerAuthenticationChallengeKey: String = "BearerAuth"

/**
 * Represents a Bearer [token]
 * @property token
 */
data class BearerCredential(val token: String) : Credential

/**
 * Retrieves Basic authentication credentials for this [ApplicationRequest]
 */
private fun ApplicationRequest.bearerAuthenticationCredentials(): BearerCredential? {
    when (val authHeader = parseAuthorizationHeader()) {
        is HttpAuthHeader.Single -> {
            // Verify the auth scheme is HTTP Bearer. According to RFC 2617, the authorization scheme should not be case
            // sensitive; thus BEARER, or Bearer, or bearer are all valid.
            if (!authHeader.authScheme.equals("Bearer", ignoreCase = true)) {
                return null
            }

            return BearerCredential(authHeader.blob)
        }
        else -> return null
    }
}

private val scopeSerializer: ScopeSerializer
    get() = ScopeSerializer()

/**
 * Generates a Bearer challenge as a [HttpAuthHeader].
 */
fun bearerAuthChallenge(realm: String, scopes: Set<Scopes>? = null) = HttpAuthHeader.Parameterized("Bearer",
    LinkedHashMap<String, String>().apply {
        put(HttpAuthHeader.Parameters.Realm, realm)
        put(HttpAuthHeader.Parameters.Charset, Charsets.UTF_8.name)
        if (scopes != null) {
            put("scopes", scopeSerializer.serialize(scopes))
        }
    }
)