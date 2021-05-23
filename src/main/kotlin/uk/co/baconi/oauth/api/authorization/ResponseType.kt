package uk.co.baconi.oauth.api.authorization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ResponseType {
    @SerialName("code") Code
}