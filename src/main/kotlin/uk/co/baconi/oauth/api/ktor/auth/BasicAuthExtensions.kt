package uk.co.baconi.oauth.api.ktor.auth

import io.ktor.auth.*
import kotlin.reflect.jvm.jvmName

/**
 * Provides a typed way to define which principle type this basic auth will provide.
 */
inline fun <reified T : Principal> Authentication.Configuration.basic(
    noinline configure: BasicAuthenticationProvider.Configuration.() -> Unit
) {
    basic(T::class.jvmName, configure)
}
