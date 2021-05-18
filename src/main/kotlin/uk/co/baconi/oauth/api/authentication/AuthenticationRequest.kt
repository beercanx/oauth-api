package uk.co.baconi.oauth.api.authentication

sealed class AuthenticationRequest {
    abstract val username: String?
    abstract val password: String?
}

data class InvalidAuthenticationCsrfToken(
    override val username: String?,
    override val password: String?
) : AuthenticationRequest()

data class InvalidAuthenticationRequest(
    override val username: String?,
    override val password: String?
) : AuthenticationRequest()

data class ValidatedAuthenticationRequest(
    override val username: String,
    override val password: String
) : AuthenticationRequest()