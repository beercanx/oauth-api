package uk.co.baconi.oauth.api.ktor.auth

import io.ktor.auth.*
import io.ktor.features.*
import uk.co.baconi.oauth.api.ktor.auth.body.BodyAuthenticationProvider
import uk.co.baconi.oauth.api.ktor.auth.body.body
import kotlin.reflect.jvm.jvmName

/**
 * Provides a typed way to define which principle type this basic auth will provide.
 * Requires the [DoubleReceive] feature to enable access to the body inside the request handler.
 */
inline fun <reified T : Principal> Authentication.Configuration.body(
    noinline configure: BodyAuthenticationProvider.Configuration.() -> Unit
) {
    body(T::class.jvmName, configure)
}
