package uk.co.baconi.oauth.api.common.authorisation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class AuthorisationResponseType(internal val value: String) {

    @SerialName("code") Code("code");

    companion object {

        fun fromValue(value: String): AuthorisationResponseType = checkNotNull(fromValueOrNull(value)) {
            "No such AuthorisationResponseType [$value]"
        }

        fun fromValueOrNull(value: String): AuthorisationResponseType? = entries.firstOrNull { type ->
            type.value == value
        }
    }
}