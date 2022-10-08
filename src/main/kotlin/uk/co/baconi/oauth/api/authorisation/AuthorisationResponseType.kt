package uk.co.baconi.oauth.api.authorisation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class AuthorisationResponseType {
    @SerialName("code") Code
}