package uk.co.baconi.oauth.api.authorisation

import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.util.*
import kotlinx.html.*
import uk.co.baconi.oauth.api.common.authentication.AuthenticateSession
import uk.co.baconi.oauth.api.common.authentication.AuthenticatedSession
import uk.co.baconi.oauth.api.common.authorisation.AuthorisationResponseType.Code
import uk.co.baconi.oauth.api.common.html.PageTemplate.base
import uk.co.baconi.oauth.api.common.html.PageTemplate.bootstrap
import uk.co.baconi.oauth.api.common.html.PageTemplate.metaData
import java.util.*

interface AuthorisationRoute : AuthorisationRequestValidation {

    val authorisationCodeService: AuthorisationCodeService

    fun Route.authorisation() {

        application.log.info("Registering the AuthorisationRoute.authorisation() routes")

        // TODO - Verify assumptions, not sure this has been done correctly
        // TODO - What about those who navigate back?
        // TODO - What about those who wish to cancel?
        // TODO - What about those logging into a new account when one already was?

        route("/authorise") {

            // TODO - How to detect user click back in the browser from the authentication screen
            get {
                when (val request = call.validateAuthorisationRequest()) {

                    // Unsafe to redirect when either client or redirect uri is invalid.
                    is AuthorisationRequest.InvalidClient, is AuthorisationRequest.InvalidRedirect -> {

                        application.log.warn("Request with invalid client or redirect: {}", request)

                        call.respondHtml(BadRequest) {
                            base()
                            head {
                                metaData()
                                bootstrap()
                                title { +"Authorise - Invalid Request" }
                            }
                            body {
                                h1(classes = "text-center") {
                                    +"Invalid Request"
                                }
                                p(classes = "text-center") {
                                    +"Invalid client or redirect was used."
                                }
                            }
                        }
                    }

                    is AuthorisationRequest.Invalid -> {

                        // Redirect with error response
                        call.respondRedirect(
                            url {
                                takeFrom(request.redirectUri)
                                parameters["error"] = request.error
                                parameters["error_description"] = request.description
                                if (request.state != null) parameters["state"] = request.state
                            }
                        )
                    }

                    // TODO - Redirect with server_error or temporarily_unavailable instead of an error page on exceptions.
                    is AuthorisationRequest.Valid -> when (request.responseType) {
                        Code -> {
                            when (val authenticated = call.sessions.get<AuthenticatedSession>()) {

                                // Seek authorisation decision
                                null -> {
                                    call.respondNullable(OK, call.resolveResource("static/authentication/index.html"))
                                }

                                // Handle authorisation decision [success]
                                else -> {
                                    val (username) = authenticated
                                    val authorisationCode = authorisationCodeService.issue(request, username)
                                    val code = authorisationCode.value.toString()
                                    val state = authorisationCode.state

                                    call.respondRedirect(
                                        url {
                                            takeFrom(request.redirectUri)
                                            parameters["code"] = code
                                            parameters["state"] = state
                                        }.also {
                                            application.log.info("Redirecting back to $it")
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
