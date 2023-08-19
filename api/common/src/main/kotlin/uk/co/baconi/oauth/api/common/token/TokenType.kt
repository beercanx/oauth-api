package uk.co.baconi.oauth.api.common.token

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Access token type as described https://www.rfc-editor.org/rfc/rfc6749#section-7.1
 */
@Serializable
enum class TokenType {
    @SerialName("bearer") Bearer;
}