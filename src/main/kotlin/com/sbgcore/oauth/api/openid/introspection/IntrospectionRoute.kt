package com.sbgcore.oauth.api.openid.introspection

import com.sbgcore.oauth.api.authentication.ConfidentialClient
import com.sbgcore.oauth.api.ktor.authenticate
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.tokenIntrospectionRoute(
    introspectionService: IntrospectionService
) {
    authenticate<ConfidentialClient> {
        post {

            val response = when (val request = validateIntrospectionRequest()) {
                is IntrospectionRequest -> introspectionService.introspect(request)
                is IntrospectionRequestWithHint -> introspectionService.introspect(request)
            }

            call.respond(response)
        }
    }
}