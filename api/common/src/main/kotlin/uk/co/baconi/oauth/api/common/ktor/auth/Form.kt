package uk.co.baconi.oauth.api.common.ktor.auth

import io.ktor.server.auth.*
import io.ktor.server.plugins.doublereceive.*
import kotlin.reflect.jvm.jvmName

/**
 * Provides a typed way to define which principle type this basic auth will provide.
 * Requires the [DoubleReceive] feature to enable access to the body inside the request handler.
 */
inline fun <reified T : Principal> AuthenticationConfig.form(
    noinline configure: FormAuthenticationProvider.Config.() -> Unit
) {
    form(T::class.jvmName, configure)
}