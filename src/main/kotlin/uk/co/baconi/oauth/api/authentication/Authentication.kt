package uk.co.baconi.oauth.api.authentication

sealed class Authentication {

    data class Success(val username: AuthenticatedUsername) : Authentication() {
        constructor(username: String) : this(AuthenticatedUsername(username))
    }

    data class Failure(val reason: Reason) : Authentication() {

        enum class Reason {
            Missing,
            Mismatched,
            Suspended,
            Closed,
            ChangePassword
        }
    }
}
