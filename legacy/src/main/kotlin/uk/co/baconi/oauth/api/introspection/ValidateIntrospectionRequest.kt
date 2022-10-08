package uk.co.baconi.oauth.api.introspection

import io.ktor.server.application.*
import io.ktor.server.request.*
import uk.co.baconi.oauth.api.client.ConfidentialClient
import uk.co.baconi.oauth.api.enums.deserialise
import uk.co.baconi.oauth.api.ktor.ApplicationContext
import uk.co.baconi.oauth.api.tokens.Tokens

private const val TOKEN = "token"
private const val TOKEN_TYPE_HINT = "token_type_hint"

suspend fun ApplicationContext.validateIntrospectionRequest(
    principal: ConfidentialClient
): IntrospectionRequest {

    val parameters = call.receiveParameters()

    val token = parameters[TOKEN]
    val hint = parameters[TOKEN_TYPE_HINT]
    val validHint = hint?.deserialise<Tokens>()

    return when {
        token == null -> IntrospectionRequest.Invalid("invalid_request", "missing parameter: token")
        token.isBlank() -> IntrospectionRequest.Invalid("invalid_request", "invalid parameter: token")

        hint.isNullOrBlank() || validHint == null -> IntrospectionRequest.Valid(principal, token)

        else -> IntrospectionRequest.ValidWithHint(principal, token, validHint)
    }
}
