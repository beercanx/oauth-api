package uk.co.baconi.oauth.api.authentication

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.sessions.*
import uk.co.baconi.oauth.api.authentication.AuthenticationRequest.Raw
import uk.co.baconi.oauth.api.common.authentication.AuthenticateSession

interface AuthenticationRequestValidation {

    suspend fun ApplicationCall.validateAuthenticationRequest(): AuthenticationRequest {
        val raw = receive<Raw>()
        val session = sessions.get<AuthenticateSession>()
        return validateAuthenticationRequest(raw, session)
    }

    fun validateAuthenticationRequest(raw: Raw, session: AuthenticateSession?): AuthenticationRequest = when {

        // Check CSRF Token
        session == null -> AuthenticationRequest.InvalidField("csrfToken")
        raw.csrfToken.isNullOrBlank() -> AuthenticationRequest.InvalidField("csrfToken")
        raw.csrfToken != session.csrfToken.toString() -> AuthenticationRequest.InvalidField("csrfToken")

        // Basic field validation
        raw.username.isNullOrBlank() -> AuthenticationRequest.InvalidField("username")
        raw.password == null -> AuthenticationRequest.InvalidField("password")
        raw.password.isEmpty() -> AuthenticationRequest.InvalidField("password")

        // Good enough to attempt an authentication
        else -> AuthenticationRequest.Valid(
            username = raw.username,
            password = raw.password,
        )
    }
}