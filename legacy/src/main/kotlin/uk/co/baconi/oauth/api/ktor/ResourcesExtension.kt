package uk.co.baconi.oauth.api.ktor

import io.ktor.server.application.*
import io.ktor.server.resources.Resources
import io.ktor.resources.*
import io.ktor.resources.serialization.*

val Application.resourcesFormat: ResourcesFormat
    get() = plugin(Resources).resourcesFormat

val ApplicationContext.resourcesFormat: ResourcesFormat
    get() = application.resourcesFormat

inline fun <reified T> ResourcesFormat.href(resource: T) = href(this, resource)

inline fun <reified T> Application.href(resource: T) = href(resourcesFormat, resource)

inline fun <reified T> ApplicationContext.href(resource: T) = href(resourcesFormat, resource)