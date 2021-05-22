package uk.co.baconi.oauth.api.authentication

sealed class Authentication {

    data class Success(val username: String) : Authentication()

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
