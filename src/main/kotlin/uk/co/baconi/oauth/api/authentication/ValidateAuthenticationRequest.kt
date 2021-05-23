package uk.co.baconi.oauth.api.authentication

import io.ktor.application.*
import io.ktor.request.*
import io.ktor.sessions.*
import uk.co.baconi.oauth.api.authentication.AuthenticationPageTemplate.Companion.ABORT
import uk.co.baconi.oauth.api.authentication.AuthenticationPageTemplate.Companion.CSRF_TOKEN
import uk.co.baconi.oauth.api.authentication.AuthenticationPageTemplate.Companion.LOGIN
import uk.co.baconi.oauth.api.authentication.AuthenticationPageTemplate.Companion.PASSWORD
import uk.co.baconi.oauth.api.authentication.AuthenticationPageTemplate.Companion.USERNAME
import uk.co.baconi.oauth.api.ktor.ApplicationContext

suspend fun ApplicationContext.validateAuthenticationRequest(): AuthenticationRequest {

    val parameters = call.receiveParameters()
    val session = call.sessions.get<AuthenticationSession>()

    val csrfToken = parameters[CSRF_TOKEN]
    val username = parameters[USERNAME]
    val password = parameters[PASSWORD]
    val abort = parameters[ABORT]

    return when {

        // Check for user abort
        abort?.isNotBlank() ?: false -> AuthenticationRequest.Aborted

        // Check CSRF Token
        session == null -> AuthenticationRequest.InvalidCsrf(username, password)
        csrfToken.isNullOrBlank() -> AuthenticationRequest.InvalidCsrf(username, password)
        session.csrfToken != csrfToken -> AuthenticationRequest.InvalidCsrf(username, password)

        // Basic field validation
        username.isNullOrBlank() -> AuthenticationRequest.InvalidFields(username, password)
        password.isNullOrBlank() -> AuthenticationRequest.InvalidFields(username, password)

        // Good enough to attempt an authentication
        else -> AuthenticationRequest.Valid(username, password)
    }
}