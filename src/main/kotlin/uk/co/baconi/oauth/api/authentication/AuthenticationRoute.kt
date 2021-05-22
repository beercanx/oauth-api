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
import uk.co.baconi.oauth.api.authorization.AuthorizationLocation
import uk.co.baconi.oauth.api.ktor.ApplicationContext
import java.util.*


interface AuthenticationRoute {

    val authenticationService: AuthenticationService

    // TODO - Consider if we need the pre-authenticated session to be created and destroyed by Authorization instead.

    private fun ApplicationContext.getAuthenticationSession(): AuthenticationSession {
        return call.sessions.getOrSet {
            application.log.debug("Creating new AuthenticationSession")
            AuthenticationSession(UUID.randomUUID())
        }
    }

    fun Route.authentication() {

        get<AuthenticationLocation> {

            renderAuthenticationPage()
        }

        post<AuthenticationLocation> {

            return@post when (val request = validateAuthenticationRequest()) {

                is AuthenticationRequest.InvalidCsrf -> {
                    renderAuthenticationPage(request, Forbidden) {
                        +"Please retry, we received an invalid CSRF token."
                    }
                }

                is AuthenticationRequest.InvalidFields -> {
                    renderAuthenticationPage(request, BadRequest) {
                        +"Please fill out and retry, we need both your username and password to log you in."
                    }
                }

                is AuthenticationRequest.Valid -> when (val result = authenticationService.authenticate(request)) {

                    is Authentication.Failure -> renderAuthenticationPage(request, Unauthorized) {
                        +"Please check and try again or if you have forgotten your details, recover them "
                        a(
                            href = "/recovery",
                            classes = "alert-link"
                        ) { +"here" } // TODO - Implement recovery mechanism?
                        +"."
                    }

                    is Authentication.Success -> {
                        // TODO - Create full session.
                        //call.sessions.set()

                        // Destroy pre-authenticated session.
                        call.sessions.clear<AuthenticationSession>()

                        // Go back to authorization
                        call.respondRedirect(href(AuthorizationLocation))
                    }
                }
            }
        }
    }

    private suspend fun ApplicationContext.renderAuthenticationPage(
        request: AuthenticationRequest? = null,
        status: HttpStatusCode = HttpStatusCode.OK,
        failureMessage: (DIV.(Placeholder<DIV>) -> Unit)? = null
    ) {
        val session = getAuthenticationSession()
        call.respondHtmlTemplate(AuthenticationPageTemplate(locations), status) {
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