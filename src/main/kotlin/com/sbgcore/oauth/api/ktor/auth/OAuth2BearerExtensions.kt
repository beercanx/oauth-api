package com.sbgcore.oauth.api.ktor.auth

import com.sbgcore.oauth.api.ktor.auth.bearer.OAuth2BearerAuthenticationProvider
import com.sbgcore.oauth.api.ktor.auth.bearer.oAuth2Bearer
import com.sbgcore.oauth.api.openid.Scopes
import io.ktor.auth.*
import io.ktor.routing.*
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmName

/**
 * Takes a [Principal] class and a set of [Scopes] to produce a unique authentication name identifier.
 */
fun <T : Principal> generateName(kClass: KClass<T>, scopes: Set<Scopes>): String {
    return when {
        scopes.isEmpty() -> kClass.jvmName
        else -> "${kClass.jvmName};${scopes.joinToString(" ")}"
    }
}

/**
 * Provides a typed way to define which principle type this basic auth will provide.
 */
inline fun <reified T : Principal> Authentication.Configuration.oAuth2Bearer(
    scopes: Set<Scopes>,
    noinline configure: OAuth2BearerAuthenticationProvider.Configuration.() -> Unit
) {
    oAuth2Bearer(generateName(T::class, scopes), configure)
}

/**
 * Provides a typed way to declare which principle type we expect for this route.
 */
inline fun <reified T : Principal> Route.authenticate(
    scopes: Set<Scopes>,
    optional: Boolean = false,
    noinline build: Route.() -> Unit
): Route {
    return authenticate(generateName(T::class, scopes), optional = optional, build = build)
}
