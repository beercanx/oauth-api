/*
 * Copyright 2014-2019 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 *
 * Origin of code base https://raw.githubusercontent.com/ktorio/ktor/1.5.2/ktor-features/ktor-auth/jvm/src/io/ktor/auth/BasicAuth.kt
 */

package com.sbgcore.oauth.api.ktor.auth.bearer

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.response.*

/**
 * Installs an OAuth2 Bearer Authentication mechanism
 */
fun Authentication.Configuration.oAuth2Bearer(
    name: String? = null,
    configure: OAuth2BearerAuthenticationProvider.Configuration.() -> Unit
) {
    val provider = OAuth2BearerAuthenticationProvider(OAuth2BearerAuthenticationProvider.Configuration(name).apply(configure))
    val realm = provider.realm
    val scopes = provider.scopes
    val authenticate = provider.authenticationFunction

    provider.pipeline.intercept(AuthenticationPipeline.RequestAuthentication) { context ->
        val credentials = call.request.oAuth2BearerAuthenticationCredentials(scopes)
        val principal = credentials?.let { authenticate(call, it) }

        val cause = when {
            credentials == null -> AuthenticationFailedCause.NoCredentials
            principal == null -> AuthenticationFailedCause.InvalidCredentials
            else -> null
        }

        if (cause != null) {
            context.challenge(oAuth2BearerAuthenticationChallengeKey, cause) {
                call.respond(UnauthorizedResponse(oAuth2BearerAuthChallenge(realm, scopes)))
                it.complete()
            }
        }
        if (principal != null) {
            context.principal(principal)
        }
    }

    register(provider)
}

private const val oAuth2BearerAuthenticationChallengeKey: String = "OAuth2BearerAuth"
