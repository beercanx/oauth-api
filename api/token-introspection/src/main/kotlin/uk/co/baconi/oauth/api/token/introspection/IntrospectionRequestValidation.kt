package uk.co.baconi.oauth.api.token.introspection

import io.ktor.server.application.*
import io.ktor.server.request.*
import uk.co.baconi.oauth.api.common.client.ClientAction.Introspect
import uk.co.baconi.oauth.api.common.client.ConfidentialClient
import uk.co.baconi.oauth.api.common.uuid.UUIDSerializer
import uk.co.baconi.oauth.api.token.introspection.IntrospectionErrorType.InvalidRequest
import uk.co.baconi.oauth.api.token.introspection.IntrospectionErrorType.UnauthorizedClient

object IntrospectionRequestValidation {

    private const val TOKEN = "token"

    suspend fun ApplicationCall.validateIntrospectionRequest(principal: ConfidentialClient): IntrospectionRequest {

        val parameters = receiveParameters()

        val token = parameters[TOKEN]
        val tokenUuid by lazy {
            UUIDSerializer.fromValueOrNull(token)
        }

        return when {
            // TODO - 403?
            !principal.can(Introspect) -> IntrospectionRequest.Invalid(UnauthorizedClient, "client is not allowed to introspect")

            token == null -> IntrospectionRequest.Invalid(InvalidRequest, "missing parameter: token")
            token.isBlank() -> IntrospectionRequest.Invalid(InvalidRequest, "invalid parameter: token")

            else -> when(val uuid = tokenUuid){
                null -> IntrospectionRequest.Invalid(InvalidRequest, "invalid parameter: token")
                else -> IntrospectionRequest.Valid(principal, uuid)
            }
        }
    }
}
