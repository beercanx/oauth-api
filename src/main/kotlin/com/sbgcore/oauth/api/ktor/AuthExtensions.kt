package com.sbgcore.oauth.api.ktor

import com.sbgcore.oauth.api.authentication.ClientPrincipal

import io.ktor.auth.Authentication
import io.ktor.auth.BasicAuthenticationProvider
import io.ktor.auth.basic
import io.ktor.auth.authenticate

import io.ktor.routing.Route

import kotlin.reflect.jvm.jvmName

/**
 * Provides a typed way to define which principle type this basic auth will provide.
 */
inline fun <reified T : ClientPrincipal> Authentication.Configuration.basic(
    noinline configure: BasicAuthenticationProvider.Configuration.() -> Unit
) {
    basic(T::class.jvmName, configure)
}

/**
 * Provides a typed way to declare which principle type we expect for this route.
 */
inline fun <reified T : ClientPrincipal> Route.authenticate(
    optional: Boolean = false,
    noinline build: Route.() -> Unit
): Route {
    return authenticate(T::class.jvmName, optional = optional, build = build)
}
