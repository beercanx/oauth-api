package uk.co.baconi.oauth.api.token.introspection

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class IntrospectionErrorType {
    @SerialName("invalid_request") InvalidRequest,
    @SerialName("unauthorized_client") UnauthorizedClient,
}