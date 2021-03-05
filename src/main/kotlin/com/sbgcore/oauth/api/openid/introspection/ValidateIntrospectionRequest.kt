package com.sbgcore.oauth.api.openid.introspection

import com.sbgcore.oauth.api.authentication.ConfidentialClient
import com.sbgcore.oauth.api.openid.validClientPrincipal
import com.sbgcore.oauth.api.openid.validateStringParameter
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.request.*
import io.ktor.util.pipeline.*

suspend fun PipelineContext<*, ApplicationCall>.validateIntrospectionRequest(): ValidatedIntrospectionRequest {

    val rawIntrospectionRequest = call.receive<RawIntrospectionRequest>()

    val principal = validClientPrincipal(call.principal<ConfidentialClient>())
    val token = rawIntrospectionRequest.validateStringParameter(RawIntrospectionRequest::token)

    val hint = rawIntrospectionRequest.hint
    return if (hint == null) {
        IntrospectionRequest(principal, token)
    } else {
        IntrospectionRequestWithHint(principal, token, hint)
    }
}
