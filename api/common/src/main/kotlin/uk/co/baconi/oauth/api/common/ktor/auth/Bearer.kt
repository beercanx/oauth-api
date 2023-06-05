package uk.co.baconi.oauth.api.common.ktor.auth

import io.ktor.server.auth.*
import uk.co.baconi.oauth.api.common.ktor.auth.bearer.BearerAuthenticationProvider
import uk.co.baconi.oauth.api.common.ktor.auth.bearer.bearer
import kotlin.reflect.jvm.jvmName

/**
 * Provides a typed way to define which principle type this bearer auth will provide.
 */
inline fun <reified T : Principal> AuthenticationConfig.bearer(
    noinline configure: BearerAuthenticationProvider.Config.() -> Unit
) {
    bearer(T::class.jvmName, configure)
}
