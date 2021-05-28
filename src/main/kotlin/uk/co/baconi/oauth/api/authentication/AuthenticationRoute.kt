package uk.co.baconi.oauth.api.authentication

import io.ktor.application.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Forbidden
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import kotlinx.html.*
import uk.co.baconi.oauth.api.ktor.ApplicationContext
import java.util.*

interface AuthenticationRoute {

    val authenticationService: AuthenticationService

    private fun ApplicationContext.getAuthenticationSession(): AuthenticationSession {
        return call.sessions.getOrSet { AuthenticationSession(UUID.randomUUID()) }
    }

    fun Route.authentication() {

        // TODO - Consider only support post and requiring the initial render to happen from the AuthorisationLocation
        get<AuthenticationLocation> { location ->
            // TODO - Do we invalidate the AuthenticatedSession on first render?
            // TODO - Consider adding support for just password entry if we have an AuthenticatedSession
            renderAuthenticationPage(location)
        }

        post<AuthenticationLocation> { location ->

            return@post when (val request = validateAuthenticationRequest(location)) {

                is AuthenticationRequest.InvalidCsrf -> {
                    renderAuthenticationPage(location, request, Forbidden) {
                        +"Please retry, we received an invalid CSRF token."
                    }
                }

                is AuthenticationRequest.InvalidFields -> {
                    renderAuthenticationPage(location, request, BadRequest) {
                        +"Please fill out and retry, we need both your username and password to log you in."
                    }
                }

                is AuthenticationRequest.Aborted -> {

                    // Destroy pre-authenticated session.
                    call.sessions.clear<AuthenticationSession>()

                    // Go to redirect uri
                    call.respondRedirect {
                        parameters.clear()
                        takeFrom(request.redirect)
                        parameters["resume"] = "false"
                    }
                }

                is AuthenticationRequest.Valid -> when (val result = authenticationService.authenticate(request)) {

                    is Authentication.Failure -> renderAuthenticationPage(location, request, Unauthorized) {
                        +"Please check and try again or if you have forgotten your details, recover them "
                        a(
                            href = "/recovery",
                            classes = "alert-link"
                        ) { +"here" } // TODO - Implement recovery mechanism?
                        +"."
                    }

                    is Authentication.Success -> {
                        // Setup the authenticated session.
                        call.sessions.set(AuthenticatedSession(result.username))

                        // Destroy pre-authenticated session.
                        call.sessions.clear<AuthenticationSession>()

                        // Go to redirect uri
                        call.respondRedirect {
                            parameters.clear()
                            takeFrom(request.redirect)
                            parameters["resume"] = "true"
                        }
                    }
                }
            }
        }
    }

    private suspend fun ApplicationContext.renderAuthenticationPage(
        location: AuthenticationLocation,
        request: AuthenticationRequest? = null,
        status: HttpStatusCode = HttpStatusCode.OK,
        failureMessage: (DIV.(Placeholder<DIV>) -> Unit)? = null
    ) {
        val session = getAuthenticationSession()

        // TODO - Move more into the template than out here.
        call.respondHtmlTemplate(AuthenticationPageTemplate(locations, location), status) {
            csrfToken {
                value = session.csrfToken
            }
            if (request != null) {
                username {
                    fillIn(request.username)
                }
                password {
                    fillIn(request.password)
                }
            }
            if (failureMessage != null) {
                beforeInput {
                    div("alert alert-danger") {
                        role = "alert"
                        insert(Placeholder<DIV>().apply { invoke(content = failureMessage) })
                    }
                }
            }
        }
    }

    // TODO - Decide if this is good or bad pattern
    private fun INPUT.fillIn(data: String?) {
        if (data.isNullOrBlank()) {
            classes = classes + "is-invalid"
        } else {
            value = data
            classes = classes + "is-valid"
        }
    }
}