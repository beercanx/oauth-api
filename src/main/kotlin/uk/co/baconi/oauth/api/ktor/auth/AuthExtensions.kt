package uk.co.baconi.oauth.api.ktor.auth

import io.ktor.auth.*
import io.ktor.routing.*
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmName

/**
 * Provides a typed way to declare which principle type we expect for this route.
 */
fun Route.authenticate(
    vararg principals: KClass<out Principal>,
    optional: Boolean = false,
    build: Route.() -> Unit
): Route {
    val configurations = principals.map(KClass<*>::jvmName).toTypedArray()
    return authenticate(*configurations, optional = optional, build = build)
}