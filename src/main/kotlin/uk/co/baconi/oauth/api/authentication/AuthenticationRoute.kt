package uk.co.baconi.oauth.api.authentication

import io.ktor.application.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import uk.co.baconi.oauth.api.authentication.AuthenticationPageTemplate.Companion.CSRF_TOKEN
import uk.co.baconi.oauth.api.authentication.AuthenticationPageTemplate.Companion.PASSWORD
import uk.co.baconi.oauth.api.authentication.AuthenticationPageTemplate.Companion.USERNAME
import uk.co.baconi.oauth.api.authentication.AuthenticationPageTemplate.Companion.csrfToken
import uk.co.baconi.oauth.api.authentication.AuthenticationPageTemplate.Companion.prefill
import uk.co.baconi.oauth.api.authorization.AuthorizationLocation
import java.util.*


interface AuthenticationRoute {

    // TODO - Consider if we need the pre-authenticated session to be created and destroyed by Authorization instead.

    // TODO - https://cheatsheetseries.owasp.org/cheatsheets/Cross-Site_Request_Forgery_Prevention_Cheat_Sheet.html#login-csrf
    // TODO - https://cheatsheetseries.owasp.org/cheatsheets/Cross-Site_Request_Forgery_Prevention_Cheat_Sheet.html#synchronizer-token-pattern

    fun Route.authentication() {

        get<AuthenticationLocation> {

            // TODO - Create pre-authenticated session

            call.respondHtmlTemplate(AuthenticationPageTemplate(locations)) {
                csrfToken(UUID.randomUUID()) // TODO - Update session value, extract into a session based generator.
            }
        }

        post<AuthenticationLocation> {

            val parameters = call.receiveParameters()

            application.log.trace("${call.request.local.uri} with: $parameters")

            // Force return so we don't accidentally place code after this block
            return@post when {

                // TODO - Convert into the pattern we've used to create the API endpoints?

                // Check csrf_token before processing anything.
                csrfTokenIsInvalid(parameters) -> {
                    call.respondHtmlTemplate(AuthenticationPageTemplate(locations), BadRequest) {
                        csrfToken(UUID.randomUUID()) // TODO - Update session value, extract into a session based generator.
                        // TODO - Display CSRF token missing error message
                    }
                }

                // Validate the form contains enough data to bother processing further.
                failsFormValidation(parameters) -> call.respondHtmlTemplate(AuthenticationPageTemplate(locations), BadRequest) {
                    csrfToken(UUID.randomUUID()) // TODO - Update session value, extract into a session based generator.
                    prefill(parameters) // TODO - Decide if this is the right thing to do security and UX wise
                }

                matchesCustomer(parameters) -> {
                    // TODO - Create full session.
                    // TODO - Destroy pre-authenticated session.
                    call.respondRedirect(href(AuthorizationLocation))
                }

                else -> {
                    call.respondHtmlTemplate(AuthenticationPageTemplate(locations), BadRequest) {
                        csrfToken(UUID.randomUUID()) // TODO - Update session value, extract into a session based generator.
                        // TODO - Display failure message, aka the generic one if they don't exist, wrong credentials or we've crapped out talking to our DB
                    }
                }
            }
        }
    }

    // TODO - Call a service to validate the customer.
    private fun matchesCustomer(parameters: Parameters): Boolean {
        return true
    }

    // TODO - Check the actual session and possibly inline
    // TODO - Will return false if there's no session
    private fun doesNotMatchSessionCsrfToken(submitted: String): Boolean {
        return false
    }

    private fun csrfTokenIsInvalid(parameters: Parameters): Boolean {
        return when(val token = parameters[CSRF_TOKEN]) {
            is String -> token.isBlank() || doesNotMatchSessionCsrfToken(token)
            else -> false
        }
    }

    // TODO - Place validation and code to apply validation to form in the same place.
    private fun failsFormValidation(parameters: Parameters): Boolean {
        return parameters[USERNAME].isNullOrBlank() || parameters[PASSWORD].isNullOrBlank()
    }
}