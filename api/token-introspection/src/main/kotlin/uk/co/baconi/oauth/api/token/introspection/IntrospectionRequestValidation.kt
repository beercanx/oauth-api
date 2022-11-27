package uk.co.baconi.oauth.api.token.introspection

import io.ktor.server.application.*
import io.ktor.server.request.*
import uk.co.baconi.oauth.api.common.client.ClientAction.Introspect
import uk.co.baconi.oauth.api.common.client.ConfidentialClient
import uk.co.baconi.oauth.api.common.uuid.UUIDSerializer
import uk.co.baconi.oauth.api.token.introspection.IntrospectionErrorType.InvalidRequest
import uk.co.baconi.oauth.api.token.introspection.IntrospectionErrorType.UnauthorizedClient
import uk.co.baconi.oauth.api.token.introspection.IntrospectionRequest.Invalid
import uk.co.baconi.oauth.api.token.introspection.IntrospectionRequest.Valid

object IntrospectionRequestValidation {

    private const val TOKEN = "token"

    suspend fun ApplicationCall.validateIntrospectionRequest(principal: ConfidentialClient): IntrospectionRequest {

        val parameters = receiveParameters()

        val token = parameters[TOKEN]
        val tokenUuid = token?.let(UUIDSerializer::fromValueOrNull)

        return when {
            // TODO - 403?
            !principal.can(Introspect) -> Invalid(UnauthorizedClient, "client is not allowed to introspect")

            token == null -> Invalid(InvalidRequest, "missing parameter: token")
            token.isBlank() -> Invalid(InvalidRequest, "invalid parameter: token")
            tokenUuid == null -> Invalid(InvalidRequest, "invalid parameter: token")

            else -> Valid(principal, tokenUuid)
        }
    }
}
