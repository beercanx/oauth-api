package uk.co.baconi.oauth.api.exchange

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class GrantType {

    @SerialName("authorization_code") AuthorisationCode,
    @SerialName("password") Password,
    @SerialName("refresh_token") RefreshToken,
    @SerialName("urn:ietf:params:oauth:grant-type:jwt-bearer") Assertion,

    ;

}