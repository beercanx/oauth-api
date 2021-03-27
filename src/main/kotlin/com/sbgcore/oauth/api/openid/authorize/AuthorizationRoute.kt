package com.sbgcore.oauth.api.openid.authorize

import com.sbgcore.oauth.api.authentication.ConfidentialClient
import com.sbgcore.oauth.api.ktor.authenticate
import com.sbgcore.oauth.api.openid.introspection.IntrospectionRequest
import com.sbgcore.oauth.api.openid.introspection.IntrospectionRequestWithHint
import com.sbgcore.oauth.api.openid.introspection.IntrospectionService
import com.sbgcore.oauth.api.openid.introspection.validateIntrospectionRequest
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
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