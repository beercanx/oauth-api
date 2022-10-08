package uk.co.baconi.oauth.common.authentication

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed class CustomerAuthentication {

    @Serializable
    @SerialName("success")
    data class Success(val username: AuthenticatedUsername) : CustomerAuthentication()

    @Serializable
    @SerialName("failure")
    data class Failure(@Transient val reason: Reason = Reason.Missing) : CustomerAuthentication() {

        enum class Reason {
            Missing,
            Mismatched,
            Closed,
            Suspended,
            Locked
        }
    }
}
