package uk.co.baconi.oauth.api.token.introspection

import uk.co.baconi.oauth.api.common.client.ConfidentialClient
import java.util.*

sealed class IntrospectionRequest {

    // TODO - Do we add types for reason and error?
    data class Invalid(val error: String, val description: String) : IntrospectionRequest() {
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
