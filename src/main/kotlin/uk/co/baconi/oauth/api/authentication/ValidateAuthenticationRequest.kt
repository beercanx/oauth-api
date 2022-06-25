package uk.co.baconi.oauth.api.authentication

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.sessions.*
import uk.co.baconi.oauth.api.authentication.AuthenticationPageTemplate.Companion.ABORT
import uk.co.baconi.oauth.api.authentication.AuthenticationPageTemplate.Companion.CSRF_TOKEN
import uk.co.baconi.oauth.api.authentication.AuthenticationPageTemplate.Companion.PASSWORD
import uk.co.baconi.oauth.api.authentication.AuthenticationPageTemplate.Companion.USERNAME
import uk.co.baconi.oauth.api.authorisation.AuthorisationLocation
import uk.co.baconi.oauth.api.ktor.ApplicationContext
import uk.co.baconi.oauth.api.ktor.href
import uk.co.baconi.oauth.api.ktor.isAbsoluteURI

suspend fun ApplicationContext.validateAuthenticationRequest(location: AuthenticationLocation): AuthenticationRequest {

    val parameters = call.receiveParameters()
    val session = call.sessions.get<AuthenticationSession>()

    val csrfToken = parameters[CSRF_TOKEN]
    val username = parameters[USERNAME]
    val password = parameters[PASSWORD]
    val abort = parameters[ABORT]

    // The redirect URI here is an "internal" redirect so it should be relative.
    val redirect = when {
        location.redirectUri.isNullOrBlank() -> href(AuthorisationLocation())
        location.redirectUri.isAbsoluteURI() -> href(AuthorisationLocation())
        else -> location.redirectUri
    }

    return when {

        // Check for user abort
        abort?.isNotBlank() ?: false -> AuthenticationRequest.Aborted(redirect)

        // Check CSRF Token
        session == null -> AuthenticationRequest.InvalidCsrf(username, password)
        csrfToken.isNullOrBlank() -> AuthenticationRequest.InvalidCsrf(username, password)
        session.csrfToken != csrfToken -> AuthenticationRequest.InvalidCsrf(username, password)

        // Basic field validation
        username.isNullOrBlank() -> AuthenticationRequest.InvalidFields(username, password)
        password.isNullOrBlank() -> AuthenticationRequest.InvalidFields(username, password)

        // Good enough to attempt an authentication
        else -> AuthenticationRequest.Valid(username, password, redirect)
    }
}
