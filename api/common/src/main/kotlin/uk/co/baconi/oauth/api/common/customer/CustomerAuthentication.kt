package uk.co.baconi.oauth.api.common.customer

import uk.co.baconi.oauth.api.common.authentication.AuthenticatedUsername

sealed interface CustomerAuthentication {

    data class Success(val username: AuthenticatedUsername) : CustomerAuthentication

    data class Failure(val reason: Reason) : CustomerAuthentication {

        // TODO - Should these reasons be extending/mirroring CustomerState or should they be safe to return types?
        enum class Reason {
            Missing,
            Mismatched,
            Closed,
            Suspended,
            Locked
        }
    }
}
