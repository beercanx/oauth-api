package uk.co.baconi.oauth.api.token.introspection

import io.ktor.server.application.*
import io.ktor.server.request.*
import uk.co.baconi.oauth.api.common.client.ConfidentialClient
import java.util.*

object IntrospectionRequestValidation {

    private const val TOKEN = "token"

    suspend fun ApplicationCall.validateIntrospectionRequest(principal: ConfidentialClient): IntrospectionRequest {

        val parameters = receiveParameters()

        val token = parameters[TOKEN]
        val tokenUuid by lazy {
            try {
                UUID.fromString(token)
            } catch (exception: IllegalArgumentException) {
                null
            }
        }

        return when {
            // TODO - 403?
            !principal.canIntrospect -> IntrospectionRequest.Invalid("unauthorized_client", "client is not allowed to introspect")

            token == null -> IntrospectionRequest.Invalid("invalid_request", "missing parameter: token")
            token.isBlank() -> IntrospectionRequest.Invalid("invalid_request", "invalid parameter: token")

            else -> when(val uuid = tokenUuid){
                null -> IntrospectionRequest.Invalid("invalid_request", "invalid parameter: token")
                else -> IntrospectionRequest.Valid(principal, uuid)
            }
        }
    }
}
