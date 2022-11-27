package uk.co.baconi.oauth.api.authorisation

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import uk.co.baconi.oauth.api.common.authorisation.AuthorisationResponseType.Code

interface AuthorisationRoute : AuthorisationRequestValidation {

    fun Route.authorisation() {

        application.log.info("Registering the AuthorisationRoute.authorisation() routes")

        route("/authorise") {
            get {
                when(val request = call.validateAuthorisationRequest()) {

                    is AuthorisationRequest.InvalidClient, is AuthorisationRequest.InvalidRedirect -> {

                        application.log.warn("Request with invalid client or redirect: {}", request)

                        // TODO - Render a 400 bad request HTML page

//                        call.respondHtmlTemplate(PageTemplate(), HttpStatusCode.BadRequest) {
//                            pageTitle {
//                                +"Invalid Request"
//                            }
//                            pageContent {
//                                h1(classes = "text-center") {
//                                    +"Invalid Request"
//                                }
//                                p(classes = "text-center") {
//                                    +"Invalid client or redirect was used."
//                                }
//                            }
//                        }
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

                    // TODO - Handle authorisation request [valid]
                    is AuthorisationRequest.Valid -> when (request.responseType) {
                        Code -> {
//                        when (val authenticated = call.sessions.get<AuthenticatedSession>()) {
//
//                            // Seek authorisation decision
//                            null -> {
//                                // Redirect to our "login" page with a redirect back to here.
//                                call.respondRedirect(href(AuthenticationLocation(href(location))))
//                            }
//
//                            // Handle authorisation decision [success]
//                            else -> {
//
//                                val authorisationCode = authorisationCodeService.issue(request, authenticated)
//
//                                call.respondRedirect(
//                                    URLBuilder().apply {
//                                        takeFrom(request.redirectUri)
//                                        parameters["code"] = authorisationCode.value
//                                        parameters["state"] = request.state
//                                    }.buildString()
//                                )
//                            }
//                        }
                        }
                    }
                }
            }
        }
    }
}