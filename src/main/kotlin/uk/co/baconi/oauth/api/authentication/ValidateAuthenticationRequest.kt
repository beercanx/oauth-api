package uk.co.baconi.oauth.api.authentication

import io.ktor.application.*
import io.ktor.request.*
import io.ktor.sessions.*
import uk.co.baconi.oauth.api.authentication.AuthenticationPageTemplate.Companion.CSRF_TOKEN
import uk.co.baconi.oauth.api.authentication.AuthenticationPageTemplate.Companion.PASSWORD
import uk.co.baconi.oauth.api.authentication.AuthenticationPageTemplate.Companion.USERNAME
import uk.co.baconi.oauth.api.ktor.ApplicationContext

suspend fun ApplicationContext.validateAuthenticationRequest(): AuthenticationRequest<*, *> {

    val parameters = call.receiveParameters()
    val session = call.sessions.get<AuthenticationSession>()

    val csrfToken = parameters[CSRF_TOKEN]
    val username = parameters[USERNAME]
    val password = parameters[PASSWORD]

    return when {

        // Check CSRF Token
        session == null -> InvalidAuthenticationCsrfToken(username, password)
        csrfToken.isNullOrBlank() -> InvalidAuthenticationCsrfToken(username, password)
        session.csrfToken != csrfToken -> InvalidAuthenticationCsrfToken(username, password)

        // Basic field validation
        username.isNullOrBlank() -> InvalidAuthenticationRequest(username, password)
        password.isNullOrBlank() -> InvalidAuthenticationRequest(username, password)

        // Good enough to attempt an authentication
        else -> ValidatedAuthenticationRequest(username, password)
    }
}