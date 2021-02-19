package com.sbgcore.oauth.api.openid.introspection

import arrow.fx.IO
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.applicative.just
import arrow.fx.extensions.io.applicativeError.raiseError
import com.sbgcore.oauth.api.authentication.ConfidentialClient
import com.sbgcore.oauth.api.authentication.ClientPrincipal
import com.sbgcore.oauth.api.openid.TokenType
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.principal
import io.ktor.request.receive
import io.ktor.util.pipeline.PipelineContext
import kotlin.reflect.KProperty1

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

// TODO - Refactor other code
fun <A : ClientPrincipal> validClientPrincipal(principal: A?): IO<A> {
    return principal?.just() ?: Exception("Invalid client").raiseError()
}

// TODO - Refactor other code
fun <T> T.validateStringParameter(parameter: KProperty1<T, String?>): IO<String> {
    // TODO - Verify this is all we need to do
    return when(val value = parameter.get(this)?.trim()) {
        null, "" -> Exception("Null or blank parameter: ${parameter.name}").raiseError()
        else -> value.just()
    }
}