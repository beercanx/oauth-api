package uk.co.baconi.oauth.api.authorisation

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import uk.co.baconi.oauth.api.authentication.AuthenticatedSession
import uk.co.baconi.oauth.api.authentication.AuthenticationLocation
import uk.co.baconi.oauth.api.authorisation.ResponseType.Code
import java.util.*

interface AuthorisationRoute {

    fun Route.authorisation() {

        // TODO - Verify assumptions, not sure this has been done correctly
        // TODO - What about those who navigate back?
        // TODO - What about those who wish to cancel?
        // TODO - What about those logging into a new account when one already was?

        // TODO - Should we just render the AuthenticationPage on the authorisation endpoint but have an isolated endpoint?

        get<AuthorisationLocation> { location ->

            // TODO - How to detect user click back in the browser from the authentication screen
            return@get when (val request = validateAuthorisationRequest(location)) {

                // Handle authorisation request [invalid] / decision [failure]
                is AuthorisationRequest.Invalid -> {

                    // Remove any stashed AuthorisationSession
                    call.sessions.clear<AuthorisationSession>()

                    // TODO https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.2.1

                    /*
                     * Error: {
                     *   error: ABC,
                     *   error_description: ABC
                     * }
                     */

                    TODO("Invalid $request")
                }

                // Handle user aborted
                is AuthorisationRequest.Aborted -> {

                    // Remove any stashed AuthorisationSession
                    call.sessions.clear<AuthorisationSession>()

                    // Return with error response
                    call.respondRedirect(
                        URLBuilder(request.redirectUri).apply {
                            parameters.append("error", "access_denied")
                            parameters.append("error_description", "User aborted")
                        }.buildString()
                    )
                }

                // Handle authorisation request [valid]
                is AuthorisationRequest.Valid -> when (request.responseType) {
                    Code -> when (val authenticated = call.sessions.get<AuthenticatedSession>()) {

                        // Seek authorisation decision
                        null -> {

                            // Stash the AuthorisationSession
                            call.sessions.set(AuthorisationSession(request))

                            // Redirect to our "login" page
                            call.respondRedirect(href(AuthenticationLocation))
                        }

                        // Handle authorisation decision [success]
                        else -> {

                            // TODO - Issue Authorisation Code
                            val authorisationCode = UUID.randomUUID().toString()

                            /*
                            val authorisationCode = authorisationCodeService.issue(authenticated)
                             */

                            // Remove any stashed AuthorisationSession
                            call.sessions.clear<AuthorisationSession>()

                            call.respondRedirect(
                                URLBuilder(request.redirectUri).apply {
                                    parameters.append("code", authorisationCode)
                                    parameters.append("state", request.state)
                                }.buildString()
                            )
                        }
                    }
                }
            }
        }
    }
}