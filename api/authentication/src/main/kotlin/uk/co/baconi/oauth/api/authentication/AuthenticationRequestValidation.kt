package uk.co.baconi.oauth.api.authentication

import io.ktor.server.application.*
import io.ktor.server.request.*

private const val COOKIE_CSRF = "Authenticate-CSRF"

interface AuthenticationRequestValidation {

    suspend fun ApplicationCall.validateAuthenticationRequest(): AuthenticationRequest {

        val raw = receive<AuthenticationRequest.Raw>()
        val expectedCsrfToken = request.cookies[COOKIE_CSRF] // TODO - Verify HTTP only?

        return when {

            // Check CSRF Token
            expectedCsrfToken.isNullOrBlank() -> AuthenticationRequest.InvalidField("csrfToken")
            raw.csrfToken.isNullOrBlank() -> AuthenticationRequest.InvalidField("csrfToken")
            raw.csrfToken != expectedCsrfToken -> AuthenticationRequest.InvalidField("csrfToken")

            // Basic field validation
            raw.username.isNullOrBlank() -> AuthenticationRequest.InvalidField("username")
            raw.password == null -> AuthenticationRequest.InvalidField("password")
            raw.password.isEmpty() -> AuthenticationRequest.InvalidField("password")

            // Good enough to attempt an authentication
            else -> AuthenticationRequest.Valid(
                username = raw.username,
                password = raw.password,
                csrfToken = raw.csrfToken
            )
        }
    }

}