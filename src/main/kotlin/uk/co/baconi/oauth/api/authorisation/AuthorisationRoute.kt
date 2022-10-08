package uk.co.baconi.oauth.api.authorisation

import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.server.locations.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.html.h1
import kotlinx.html.p
import uk.co.baconi.oauth.api.authentication.AuthenticatedSession
import uk.co.baconi.oauth.api.authentication.AuthenticationLocation
import uk.co.baconi.oauth.api.authorisation.AuthorisationResponseType.Code
import uk.co.baconi.oauth.api.client.ClientConfigurationRepository
import uk.co.baconi.oauth.api.kotlinx.html.PageTemplate

interface AuthorisationRoute {

    val authorisationCodeService: AuthorisationCodeService
    val clientConfigurationRepository: ClientConfigurationRepository

    fun Route.authorisation() {

        // TODO - Verify assumptions, not sure this has been done correctly
        // TODO - What about those who navigate back?
        // TODO - What about those who wish to cancel?
        // TODO - What about those logging into a new account when one already was?

        // TODO - Should we just render the AuthenticationPage on the authorisation endpoint but have an isolated endpoint?

        get<AuthorisationLocation> { location ->

            // TODO - How to detect user click back in the browser from the authentication screen
            return@get when (val request = validateAuthorisationRequest(location, clientConfigurationRepository)) {

                // Unsafe to redirect when either client or redirect uri is invalid.
                is AuthorisationRequest.InvalidClient, is AuthorisationRequest.InvalidRedirect -> {

                    application.log.warn("Request with invalid client or redirect: {}", location)

                    call.respondHtmlTemplate(PageTemplate(), BadRequest) {
                        pageTitle {
                            +"Invalid Request"
                        }
                        pageContent {
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
                        URLBuilder().apply {
                            takeFrom(request.redirectUri)
                            parameters["error"] = request.error
                            parameters["error_description"] = request.description
                            if (request.state != null) parameters["state"] = request.state
                        }.buildString()
                    )
                }

                // Handle authorisation request [valid]
                is AuthorisationRequest.Valid -> when (request.responseType) {
                    Code -> when (val authenticated = call.sessions.get<AuthenticatedSession>()) {

                        // Seek authorisation decision
                        null -> {

                            // Redirect to our "login" page with a redirect back to here.
                            call.respondRedirect(href(AuthenticationLocation(href(location))))
                        }

                        // Handle authorisation decision [success]
                        else -> {

                            val authorisationCode = authorisationCodeService.issue(request, authenticated)

                            call.respondRedirect(
                                URLBuilder().apply {
                                    takeFrom(request.redirectUri)
                                    parameters["code"] = authorisationCode.value
                                    parameters["state"] = request.state
                                }.buildString()
                            )
                        }
                    }
                }
            }
        }
    }
}