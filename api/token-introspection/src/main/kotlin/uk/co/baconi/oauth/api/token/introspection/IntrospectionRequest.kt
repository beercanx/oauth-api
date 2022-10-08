package uk.co.baconi.oauth.api.token.introspection

import uk.co.baconi.oauth.api.common.client.ConfidentialClient
import java.util.*

sealed class IntrospectionRequest {

    data class Invalid(val error: IntrospectionErrorType, val description: String) : IntrospectionRequest() {
        fun toResponse() = IntrospectionResponse.Invalid(error, description)
    }

    data class Valid(
        val principal: ConfidentialClient,
        val token: UUID
    ) : IntrospectionRequest() {
        override fun toString(): String {
            return "IntrospectionRequest(principal=$principal, token='REDACTED')"
        }
    }
}
