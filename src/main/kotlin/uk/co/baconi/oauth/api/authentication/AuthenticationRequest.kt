package uk.co.baconi.oauth.api.authentication

sealed class AuthenticationRequest<A, B> {
    abstract val username: A
    abstract val password: B
}

data class InvalidAuthenticationCsrfToken(
    override val username: String?,
    override val password: String?
) : AuthenticationRequest<String?, String?>()

data class InvalidAuthenticationRequest(
    override val username: String?,
    override val password: String?
) : AuthenticationRequest<String?, String?>()

data class ValidatedAuthenticationRequest(
    override val username: String,
    override val password: String
) : AuthenticationRequest<String, String>()