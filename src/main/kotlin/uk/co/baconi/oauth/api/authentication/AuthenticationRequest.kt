package uk.co.baconi.oauth.api.authentication

sealed class AuthenticationRequest {

    abstract val username: String?
    abstract val password: String?

    data class InvalidCsrf(override val username: String?, override val password: String?) : AuthenticationRequest()

    data class InvalidFields(override val username: String?, override val password: String?) : AuthenticationRequest()

    data class Aborted(val redirect: String): AuthenticationRequest() {
        override val username: String? = null
        override val password: String? = null
    }

    data class Valid(
        override val username: String,
        override val password: String,
        val redirect: String
    ) : AuthenticationRequest()
}
