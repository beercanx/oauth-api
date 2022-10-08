package uk.co.baconi.oauth.api.tokens

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class TokenType {

    @SerialName("bearer") Bearer,

    ;

}