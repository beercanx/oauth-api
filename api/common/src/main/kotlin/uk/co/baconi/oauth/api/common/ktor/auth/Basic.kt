package uk.co.baconi.oauth.api.common.ktor.auth

import io.ktor.server.auth.*
import kotlin.reflect.jvm.jvmName

/**
 * Provides a typed way to define which principle type this basic auth will provide.
 */
inline fun <reified T> AuthenticationConfig.basic(
    noinline configure: BasicAuthenticationProvider.Config.() -> Unit
) {
    basic(T::class.jvmName, configure)
}
