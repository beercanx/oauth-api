/*
 * Copyright 2014-2019 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 *
 * Origin of code base https://raw.githubusercontent.com/ktorio/ktor/1.5.2/ktor-features/ktor-auth/jvm/src/io/ktor/auth/BasicAuth.kt
 */

package com.sbgcore.oauth.api.ktor.auth.bearer

import com.sbgcore.oauth.api.openid.Scopes
import io.ktor.application.*
import io.ktor.auth.*

/**
 * Represents a Bearer authentication provider
 * @property name is the name of the provider, or `null` for a default provider
 */
class OAuth2BearerAuthenticationProvider internal constructor(
    configuration: Configuration
) : AuthenticationProvider(configuration) {

    internal val realm: String? = configuration.realm

    internal val scopes: Set<Scopes> = configuration.scopes

    internal val authenticationFunction = configuration.authenticationFunction

    /**
     * Basic auth configuration
     */
    class Configuration internal constructor(name: String?) : AuthenticationProvider.Configuration(name) {

        internal var authenticationFunction: AuthenticationFunction<OAuth2BearerCredential> = {
            throw NotImplementedError(
                "Bearer auth validate function is not specified. Use bearer { validate { ... } } to fix."
            )
        }

        /**
         * Specifies realm to be passed in `WWW-Authenticate` header
         */
        var realm: String? = null

        /**
         * Specifies the minimum required scopes.
         */
        var scopes: Set<Scopes> = emptySet()

        /**
         * Sets a validation function that will check given [OAuth2BearerCredential] instance and return [Principal],
         * or null if credential does not correspond to an authenticated principal
         */
        fun validate(body: suspend ApplicationCall.(OAuth2BearerCredential) -> Principal?) {
            authenticationFunction = body
        }
    }
}
