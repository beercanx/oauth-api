package com.sbgcore.oauth.api.ktor

import io.ktor.routing.ParameterRouteSelector
import io.ktor.routing.Route
import io.ktor.util.pipeline.ContextDsl

private fun createParameterRouteSelector(route: Route, name: String) = route.createChild(ParameterRouteSelector(name))

@ContextDsl
fun Route.params(vararg names: String, build: Route.() -> Unit): Route {
    return names.fold(this, ::createParameterRouteSelector).apply(build)
}