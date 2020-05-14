package com.sbgcore.oauth.api.openid.introspection

import arrow.core.Either
import arrow.core.extensions.fx
import com.sbgcore.oauth.api.authentication.AuthenticatedClientPrincipal
import com.sbgcore.oauth.api.openid.TokenType
import com.sbgcore.oauth.api.openid.validClientPrincipal
import com.sbgcore.oauth.api.openid.validParameter
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.principal
import io.ktor.request.receive
import io.ktor.util.pipeline.PipelineContext

suspend fun PipelineContext<*, ApplicationCall>.validateIntrospectionRequest(): Either<Throwable, ValidatedIntrospectionRequest> {

    // TODO - Handle deserialisation errors
    val rawRequest = call.receive<IntrospectionRequest>()

    return Either.fx {

        val (principal) = validClientPrincipal(call.principal<AuthenticatedClientPrincipal>())
        val (token) = validParameter("token", rawRequest.token)

        val hint: TokenType? = optionalTokenHint(rawRequest)
        if(hint == null) {
            IntrospectionRequestBasic(principal, token)
        } else {
            IntrospectionRequestWithHint(principal, token, hint)
        }
    }
}

fun optionalTokenHint(request: IntrospectionRequest): TokenType? {
    return if(!request.hint.isNullOrBlank()) {
        enumValues<TokenType>().firstOrNull { token -> token.name == request.hint }
    } else {
        null
    }
}