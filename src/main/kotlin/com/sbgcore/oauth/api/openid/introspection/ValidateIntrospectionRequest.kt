package com.sbgcore.oauth.api.openid.introspection

import arrow.fx.IO
import arrow.fx.extensions.fx
import com.sbgcore.oauth.api.authentication.ConfidentialClient
import com.sbgcore.oauth.api.openid.TokenType
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

        val hint: TokenType? = optionalTokenHint(rawIntrospectionRequest)
        if (hint == null) {
            IntrospectionRequest(principal, token)
        } else {
            IntrospectionRequestWithHint(principal, token, hint)
        }
    }
}

fun optionalTokenHint(request: RawIntrospectionRequest): TokenType? {
    return if (!request.hint.isNullOrBlank()) {
        enumValues<TokenType>().firstOrNull { token -> token.name == request.hint }
    } else {
        null
    }
}