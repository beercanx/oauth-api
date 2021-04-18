package com.sbgcore.oauth.api.openid.introspection

import com.sbgcore.oauth.api.client.ConfidentialClient
import com.sbgcore.oauth.api.ktor.auth.authenticate
import com.sbgcore.oauth.api.ktor.auth.requireClient
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.introspectionRoute(
    introspectionService: IntrospectionService
) {
    authenticate<ConfidentialClient> {
        post {
            requireClient<ConfidentialClient> { principal ->

                val response = when (val request = validateIntrospectionRequest(principal)) {
                    is IntrospectionRequest -> introspectionService.introspect(request)
                    is IntrospectionRequestWithHint -> introspectionService.introspect(request)
                }

                call.respond(response)
            }
        }
    }
}