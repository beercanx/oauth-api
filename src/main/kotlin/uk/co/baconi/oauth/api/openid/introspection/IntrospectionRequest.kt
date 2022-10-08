package uk.co.baconi.oauth.api.openid.introspection

import uk.co.baconi.oauth.api.client.ConfidentialClient
import uk.co.baconi.oauth.api.tokens.Tokens
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RawIntrospectionRequest(
    val token: String?,
    @SerialName("token_type_hint") val hint: Tokens? = null
)

sealed class ValidatedIntrospectionRequest {
    abstract val principal: ConfidentialClient
    abstract val token: String
}

data class IntrospectionRequest(
    override val principal: ConfidentialClient,
    override val token: String
) : ValidatedIntrospectionRequest() {
    override fun toString(): String {
        return "IntrospectionRequest(principal=$principal, token='REDACTED')"
    }
}

data class IntrospectionRequestWithHint(
    override val principal: ConfidentialClient,
    override val token: String,
    val hint: Tokens
) : ValidatedIntrospectionRequest() {
    override fun toString(): String {
        return "IntrospectionRequestWithHint(principal=$principal, token='REDACTED', hint=$hint)"
    }
}
