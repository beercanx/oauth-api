package uk.co.baconi.oauth.api.authorisation

import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK
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
import uk.co.baconi.oauth.api.common.html.ReactTemplate.reactPage
import uk.co.baconi.oauth.api.common.location.Location
import java.util.*
import kotlin.time.Duration.Companion.minutes

private const val COOKIE_CSRF = "Authenticate-CSRF"
private const val COOKIE_CUSTOMER = "Authenticated-Customer"

interface AuthorisationRoute : AuthorisationRequestValidation {

    val authorisationCodeService: AuthorisationCodeService

    fun Route.authorisation() {

        application.log.info("Registering the AuthorisationRoute.authorisation() routes")

        val authenticationBundle = url {
            takeFrom(Location.Assets.baseUrl)
            path("/assets/js/authentication.js")
        }

        application.log.debug("Authentication asset location: {}", authenticationBundle)

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

                                    val csrfToken = UUID.randomUUID()

                                    // TODO - Convert to signed client session or switch to passed JWT.
                                    // call.sessions.set<AuthenticateSession>(AuthenticateSession(csrfToken))
                                    call.response.cookies.append(
                                        name = COOKIE_CSRF,
                                        value = csrfToken.toString(),
                                        maxAge = 30.minutes.inWholeSeconds,
                                        //secure = true, // TODO - Enable if behind TLS, may need https://ktor.io/docs/forward-headers.html
                                        httpOnly = true,
                                    )

                                    call.respondHtml(OK) {
                                        reactPage("Login Page", authenticationBundle, csrfToken)
                                    }
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