package uk.co.baconi.oauth.common.authentication

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class CustomerAuthentication {

    @Serializable
    @SerialName("success")
    data class Success(val username: AuthenticatedUsername) : CustomerAuthentication()

    @Serializable
    @SerialName("failure")
    data class Failure(val reason: Reason) : CustomerAuthentication() {

        @Serializable
        enum class Reason {
            Missing,
            Mismatched,
            Closed,
            Suspended,
            Locked
        }
    }
}
