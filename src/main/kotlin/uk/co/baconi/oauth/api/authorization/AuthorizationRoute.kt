package uk.co.baconi.oauth.api.authorization

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import uk.co.baconi.oauth.api.authentication.AuthenticatedSession
import uk.co.baconi.oauth.api.authentication.AuthenticationLocation
import uk.co.baconi.oauth.api.authorization.ResponseType.Code
import java.util.*

interface AuthorizationRoute {

    fun Route.authorization() {

        // TODO - Verify assumptions, not sure this has been done correctly
        // TODO - What about those who navigate back?
        // TODO - What about those who wish to cancel?
        // TODO - What about those logging into a new account when one already was?

        // TODO - Should we just render the AuthenticationPage on the authorization endpoint but have an isolated endpoint?

        get<AuthorizationLocation> { location ->

            return@get when (val request = validateAuthorizationRequest(location)) {

                // Handle authorization request [invalid] / decision [failure]
                is AuthorizationRequest.Invalid -> {

                    // TODO https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.2.1

                    /*
                     * Error: {
                     *   error: ABC,
                     *   error_description: ABC
                     * }
                     */

                    // Remove any stashed AuthorizationSession
                    call.sessions.clear<AuthorizationSession>()

                    TODO("Invalid $request")
                }

                // Handle cancelled / aborted
                /*
                is AuthorizationRequest.Aborted -> {

                    // Return with error response
                    call.respondRedirect {
                        takeFrom(request.redirectUri)
                        parameters.append("error", "access_denied")
                        parameters.append("error_description", "User opted to not login")
                    }

                    // Remove any stashed AuthorizationSession
                    call.sessions.clear<AuthorizationSession>()
                }
                 */

                // Handle authorization request [valid]
                is AuthorizationRequest.Valid -> when(request.responseType) {
                    Code -> when(val authenticated = call.sessions.get<AuthenticatedSession>()) {

                        // Seek authorization decision
                        null -> {

                            // Stash the AuthorizationSession
                            call.sessions.set(AuthorizationSession(request))

                            // Redirect to our "login" page
                            call.respondRedirect(href(AuthenticationLocation))
                        }

                        // Handle authorization decision [success]
                        else -> {

                            // TODO - Issue Authorization Code
                            val authorizationCode = UUID.randomUUID().toString()

                            /*
                            val authorizationCode = authorizationCodeService.issue(authenticated)
                             */

                            call.respondRedirect {
                                takeFrom(request.redirectUri)
                                parameters.append("code", authorizationCode)
                                parameters.append("state", request.state)
                            }

                            // Remove any stashed AuthorizationSession
                            call.sessions.clear<AuthorizationSession>()
                        }
                    }
                }
            }
        }
    }
}