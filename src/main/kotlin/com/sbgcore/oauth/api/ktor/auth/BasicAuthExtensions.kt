package com.sbgcore.oauth.api.ktor.auth

import io.ktor.auth.*
import io.ktor.routing.*
import kotlin.reflect.jvm.jvmName

/**
 * Provides a typed way to define which principle type this basic auth will provide.
 */
inline fun <reified T : Principal> Authentication.Configuration.basic(
    noinline configure: BasicAuthenticationProvider.Configuration.() -> Unit
) {
    basic(T::class.jvmName, configure)
}

/**
 * Provides a typed way to declare which principle type we expect for this route.
 */
inline fun <reified T : Principal> Route.authenticate(
    optional: Boolean = false,
    noinline build: Route.() -> Unit
): Route {
    return authenticate(T::class.jvmName, optional = optional, build = build)
}
