package com.sbgcore.oauth.api.openid.authorize

import io.ktor.application.*
import io.ktor.routing.*

fun Route.authorizationRoute() {
    get {

        // https://tools.ietf.org/html/rfc6749#section-4.1.1
        when(val responseType = call.request.queryParameters["response_type"]) {
            "code" -> TODO("Implement $responseType")
            else -> TODO("unsupported_response_type $responseType")
        }

        // TODO - Use typed location with query parameter extraction
        // TODO - Switch based on response_type

        /**
         * Success: {
         *   code: ABC,
         *   state: ABC
         * }
         *
         * Error: {
         *   error: ABC,
         *   error_description: ABC
         * }
         */
    }
}