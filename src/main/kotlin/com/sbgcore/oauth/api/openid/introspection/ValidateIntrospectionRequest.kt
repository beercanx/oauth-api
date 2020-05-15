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

    // TODO - Handle deserialization errors
    val rawIntrospectionRequest = call.receive<RawIntrospectionRequest>()

    return Either.fx {

        val (principal) = validClientPrincipal(call.principal<AuthenticatedClientPrincipal>())
        val (token) = validParameter("token", rawIntrospectionRequest.token)

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