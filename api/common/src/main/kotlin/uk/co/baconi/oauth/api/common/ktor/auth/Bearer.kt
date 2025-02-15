package uk.co.baconi.oauth.api.common.ktor.auth

import io.ktor.server.auth.*
import kotlin.reflect.jvm.jvmName

/**
 * Provides a typed way to define which principle type this bearer auth will provide.
 */
inline fun <reified T> AuthenticationConfig.bearer(
    noinline configure: BearerAuthenticationProvider.Config.() -> Unit
) {
    bearer(T::class.jvmName, configure)
}
