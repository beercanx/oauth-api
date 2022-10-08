package uk.co.baconi.oauth.api.authentication

import io.ktor.application.*
import io.ktor.html.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Forbidden
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import kotlinx.html.INPUT
import kotlinx.html.classes
import uk.co.baconi.oauth.api.authorization.AuthorizationLocation
import uk.co.baconi.oauth.api.ktor.ApplicationContext
import java.util.*


interface AuthenticationRoute {

    // TODO - Consider if we need the pre-authenticated session to be created and destroyed by Authorization instead.

    private fun ApplicationContext.getAuthenticationSession(): AuthenticationSession {
        return call.sessions.getOrSet { AuthenticationSession(UUID.randomUUID()) }
    }

    // TODO - Decide if this is good or bad pattern
    private fun INPUT.fillIn(data: String?) {
        if(data.isNullOrBlank()) {
            classes = classes + "is-invalid"
        } else {
            value = data
            classes = classes + "is-valid"
        }
    }

    fun Route.authentication() {

        get<AuthenticationLocation> {

            val session = getAuthenticationSession()

            call.respondHtmlTemplate(AuthenticationPageTemplate(locations)) {
                csrfToken {
                    value = session.csrfToken
                }
            }
        }

        post<AuthenticationLocation> {

            val session = getAuthenticationSession()

            // Force return so we don't accidentally place code after this block
            return@post when(val request = validateAuthenticationRequest()) {

                // TODO - Can the rendering requirements be stream lined but still readable?

                is InvalidAuthenticationCsrfToken -> {
                    call.respondHtmlTemplate(AuthenticationPageTemplate(locations), Forbidden) {
                        csrfToken {
                            value = session.csrfToken
                        }
                        username {
                            fillIn(request.username)
                        }
                        password {
                            fillIn(request.password)
                        }
                        // TODO - Display error message(s)
                        //      Please retry, we received an invalid CSRF token.
                    }
                }

                is InvalidAuthenticationRequest -> {
                    call.respondHtmlTemplate(AuthenticationPageTemplate(locations), BadRequest) {
                        csrfToken {
                            value = session.csrfToken
                        }
                        username {
                            fillIn(request.username)
                        }
                        password {
                            fillIn(request.password)
                        }
                        // TODO - Display error message(s)
                        //      Please retry, we received
                    }
                }

                is ValidatedAuthenticationRequest -> when { // TODO - Call authentication service

                    // TODO - FailedLogin
                    doesNotMatchCustomer(request) -> {
                        call.respondHtmlTemplate(AuthenticationPageTemplate(locations), Unauthorized) {
                            csrfToken {
                                value = session.csrfToken
                            }
                            username {
                                fillIn(request.username)
                            }
                            password {
                                fillIn(request.password)
                            }
                            // TODO - Display failure message(s)
                            //      Please check and try again or if you have forgotten your details recover them here.
                        }
                    }

                    // TODO - SuccessfulLogin
                    else -> {
                        // TODO - Create full session.
                        // TODO - Destroy pre-authenticated session.
                        call.respondRedirect(href(AuthorizationLocation))
                    }
                }
            }

            /*
            when(val request = validateAuthenticationRequest(session, parameters)) {
                is ValidLoginRequest -> when(val result = authenticationService.authenticate(request)) {
                    is FailedLogin -> call.respondHtmlTemplate(AuthenticationPageTemplate(locations), BadRequest) {
                        csrfToken(session.csrfToken)
                        // TODO - Display failure message, aka the generic one if they don't exist, wrong credentials or we've crapped out talking to our DB
                    }
                    is SuccessfulLogin -> {
                        // TODO - Create full session.
                        // TODO - Destroy pre-authenticated session.
                        call.respondRedirect(href(AuthorizationLocation))
                    }
                }
            }
             */
        }
    }

    // TODO - Call a service to validate the customer.
    private fun doesNotMatchCustomer(request: ValidatedAuthenticationRequest): Boolean {
        return false
    }
}