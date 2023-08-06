package uk.co.baconi.oauth.api.authorisation

import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlinx.html.*
import uk.co.baconi.oauth.api.common.authentication.AuthenticatedUsername
import uk.co.baconi.oauth.api.common.authorisation.AuthorisationResponseType.Code
import uk.co.baconi.oauth.api.common.html.PageTemplate.base
import uk.co.baconi.oauth.api.common.html.PageTemplate.bootstrap
import uk.co.baconi.oauth.api.common.html.PageTemplate.metaData
import uk.co.baconi.oauth.api.common.location.Location
import kotlin.time.Duration.Companion.minutes

private const val COOKIE_REDIRECT_URI = "Authenticate-Redirect-Uri"
private const val COOKIE_CUSTOMER = "Authenticated-Customer"

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
                when(val request = call.validateAuthorisationRequest()) {

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

                    is AuthorisationRequest.Valid -> when (request.responseType) {
                        Code -> {
                            // TODO - Convert to either a session (server/client) or manually pass a JWT
                            //when (val authenticated = call.sessions.get<AuthenticatedSession>()) {
                            when (val authenticated = call.request.cookies[COOKIE_CUSTOMER]?.let(::AuthenticatedUsername)) {

                                // Seek authorisation decision
                                null -> {

                                    // TODO - Change to rendering the authentication page here.
                                    //        As this will remove the need for messy redirects that need validating and tracking.

                                    // Redirect to our "login" page with a redirect back to here.
                                    val currentURl = call.url() // TODO - Rethink as it can change the current domain
                                    call.response.cookies.append(
                                        name = COOKIE_REDIRECT_URI,
                                        value = currentURl,
                                        maxAge = 30.minutes.inWholeSeconds,
                                        //secure = true, // TODO - Enable if behind TLS, may need https://ktor.io/docs/forward-headers.html
                                        httpOnly = true,
                                    )
                                    call.respondRedirect(
                                        url {
                                            takeFrom(Location.Authentication.baseUrl)
                                            path("/authentication")
                                        }
                                    )
                                }

                                // Handle authorisation decision [success]
                                else -> {
                                    // TODO - Don't just issue an AuthorisationCode off the back of a raw string value...
                                    val authorisationCode = authorisationCodeService.issue(request, authenticated)
                                    val code = authorisationCode.value.toString()
                                    val state = authorisationCode.state

                                    call.respondRedirect(
                                        url {
                                            takeFrom(request.redirectUri)
                                            parameters["code"] = code
                                            if (state != null) parameters["state"] = state
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