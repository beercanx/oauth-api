package uk.co.baconi.oauth.api.authentication

import io.ktor.http.ContentType.Application
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.http.HttpStatusCode.Companion.UnsupportedMediaType
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import uk.co.baconi.oauth.api.authentication.AuthenticationRequest.InvalidField
import uk.co.baconi.oauth.api.authentication.AuthenticationRequest.Valid
import uk.co.baconi.oauth.api.common.authentication.AuthenticateSession
import uk.co.baconi.oauth.api.common.authentication.AuthenticatedSession
import uk.co.baconi.oauth.api.common.authentication.CustomerAuthentication
import uk.co.baconi.oauth.api.common.authentication.CustomerAuthentication.Failure
import uk.co.baconi.oauth.api.common.authentication.CustomerAuthentication.Success
import uk.co.baconi.oauth.api.common.authentication.CustomerAuthenticationService
import java.util.*

interface AuthenticationRoute : AuthenticationRequestValidation {

    val customerAuthenticationService: CustomerAuthenticationService

    private fun authenticate(username: String, password: CharArray): CustomerAuthentication {
        return customerAuthenticationService.authenticate(username, password)
    }

    fun Route.authentication() {

        application.log.info("Registering the AuthenticationRoute.authentication() routes")

        route("/authentication") {
            route("/session") {
                accept(Application.Json) {
                    get {
                        val session = call.sessions.getOrSet { AuthenticateSession(UUID.randomUUID()) }
                        application.log.debug("{}", session)
                        call.respond(OK, buildJsonObject {
                            put("csrfToken", session.csrfToken.toString())
                        })
                    }
                }
            }
            contentType(Application.Json) {
                post {
                    when (val request = call.validateAuthenticationRequest()) {

                        is InvalidField -> call.respond<CustomerAuthentication>(BadRequest, Failure()).also {
                            application.log.debug("{}", request)
                        }

                        is Valid -> when (val result = authenticate(request.username, request.password)) {
                            is Failure -> call.respond<CustomerAuthentication>(Unauthorized, result).also {
                                application.log.debug("{}", result)
                            }

                            is Success -> {

                                // Set up the authenticated session.
                                call.sessions.set(AuthenticatedSession(result.username))

                                // Destroy pre-authenticated session.
                                call.sessions.clear<AuthenticateSession>()

                                call.respond<CustomerAuthentication>(OK, result)
                            }
                        }
                    }
                }
            }
            post {
                call.respond(UnsupportedMediaType)
            }
        }
    }
}