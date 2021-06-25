package uk.co.baconi.oauth.api.introspection

import uk.co.baconi.oauth.api.client.ConfidentialClient
import uk.co.baconi.oauth.api.tokens.Tokens

sealed class IntrospectionRequest {

    data class Invalid(val error: String, val description: String) : IntrospectionRequest() {
        fun toResponse() = InvalidIntrospectionResponse(error, description)
    }

    data class Valid(
        val principal: ConfidentialClient,
        val token: String
    ) : IntrospectionRequest() {
        override fun toString(): String {
            return "IntrospectionRequest(principal=$principal, token='REDACTED')"
        }
    }

    data class ValidWithHint(
        val principal: ConfidentialClient,
        val token: String,
        val hint: Tokens
    ) : IntrospectionRequest() {
        override fun toString(): String {
            return "IntrospectionRequestWithHint(principal=$principal, token='REDACTED', hint=$hint)"
        }
    }
}
