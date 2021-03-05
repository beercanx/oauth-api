package com.sbgcore.oauth.api.openid.introspection

import arrow.fx.IO
import arrow.fx.extensions.fx
import com.sbgcore.oauth.api.authentication.ConfidentialClient
import com.sbgcore.oauth.api.openid.validClientPrincipal
import com.sbgcore.oauth.api.openid.validateStringParameter
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.principal
import io.ktor.request.receive
import io.ktor.util.pipeline.PipelineContext

suspend fun PipelineContext<*, ApplicationCall>.validateIntrospectionRequest(): IO<ValidatedIntrospectionRequest> {
    return IO.fx {

        val rawIntrospectionRequest = !effect { call.receive<RawIntrospectionRequest>() }

        val principal = !validClientPrincipal(call.principal<ConfidentialClient>())
        val token = !rawIntrospectionRequest.validateStringParameter(RawIntrospectionRequest::token)

        val hint = rawIntrospectionRequest.hint
        if (hint == null) {
            IntrospectionRequest(principal, token)
        } else {
            IntrospectionRequestWithHint(principal, token, hint)
        }
    }
}

// TODO - Remove for good, once we know passing aardvark and getting a 400 is not against the spec.
//fun optionalTokenHint(request: RawIntrospectionRequest): TokenTypes? {
//    return if (!request.hint.isNullOrBlank()) {
//        enumValues<TokenTypes>().firstOrNull { token -> token.name == request.hint }
//    } else {
//        null
//    }
//}