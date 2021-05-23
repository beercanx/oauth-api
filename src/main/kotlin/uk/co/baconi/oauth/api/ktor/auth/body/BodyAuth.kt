/*
 * Copyright 2014-2020 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */
package uk.co.baconi.oauth.api.ktor.auth.body

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.auth.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.utils.io.charsets.*

/**
 * Represents a Body authentication provider
 * @property name is the name of the provider, or `null` for a default provider
 *
 * Requires the [DoubleReceive] feature to enable access to the body inside the request handler.
 */
class BodyAuthenticationProvider internal constructor(
    configuration: Configuration
) : AuthenticationProvider(configuration) {

    internal val realm: String = configuration.realm

    internal val userParamName: String = configuration.userParamName

    internal val passwordParamName: String = configuration.passwordParamName

    internal val authenticationFunction = configuration.authenticationFunction

    /**
     * Body auth configuration
     */
    class Configuration internal constructor(name: String?) : AuthenticationProvider.Configuration(name) {

        internal var authenticationFunction: AuthenticationFunction<UserPasswordCredential> = {
            throw NotImplementedError(
                "Body auth validate function is not specified. Use body { validate { ... } } to fix."
            )
        }

        /**
         * Specifies realm to be passed in `WWW-Authenticate` header
         */
        var realm: String = "Ktor Server"

        /**
         * POST parameter to fetch for a user name
         */
        var userParamName: String = "user"

        /**
         * POST parameter to fetch for a user password
         */
        var passwordParamName: String = "password"

        /**
         * Sets a validation function that will check given [UserPasswordCredential] instance and return [Principal],
         * or null if credential does not correspond to an authenticated principal
         */
        fun validate(body: suspend ApplicationCall.(UserPasswordCredential) -> Principal?) {
            authenticationFunction = body
        }
    }
}

/**
 * Installs Body Authentication mechanism
 */
fun Authentication.Configuration.body(
    name: String? = null,
    configure: BodyAuthenticationProvider.Configuration.() -> Unit
) {
    val provider = BodyAuthenticationProvider(BodyAuthenticationProvider.Configuration(name).apply(configure))
    val realm = provider.realm
    val authenticate = provider.authenticationFunction

    provider.pipeline.intercept(AuthenticationPipeline.RequestAuthentication) { context ->
        val postParameters = call.receiveOrNull<Parameters>()
        val username = postParameters?.get(provider.userParamName)
        val password = postParameters?.get(provider.passwordParamName)
        val credentials = if (username != null && password != null) UserPasswordCredential(username, password) else null

        val principal = credentials?.let { authenticate(call, it) }

        val cause = when {
            credentials == null -> AuthenticationFailedCause.NoCredentials
            principal == null -> AuthenticationFailedCause.InvalidCredentials
            else -> null
        }

        if (cause != null) {
            context.challenge(bodyAuthenticationChallengeKey, cause) {
                call.respond(UnauthorizedResponse(bothAuthChallenge(realm)))
                it.complete()
            }
        }
        if (principal != null) {
            context.principal(principal)
        }
    }

    register(provider)
}

private val bodyAuthenticationChallengeKey: Any = "BodyAuth"

/**
 * Generates a Body challenge as a [HttpAuthHeader].
 */
private fun bothAuthChallenge(realm: String) = HttpAuthHeader.Parameterized("Body",
    LinkedHashMap<String, String>().apply {
        put(HttpAuthHeader.Parameters.Realm, realm)
        put(HttpAuthHeader.Parameters.Charset, Charsets.UTF_8.name)
    }
)