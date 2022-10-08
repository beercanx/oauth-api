package uk.co.baconi.oauth.api.authentication

import io.ktor.http.ContentType.Application
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.http.HttpStatusCode.Companion.UnsupportedMediaType
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import uk.co.baconi.oauth.api.common.authentication.CustomerAuthenticationService
import uk.co.baconi.oauth.api.common.authentication.CustomerAuthentication
import uk.co.baconi.oauth.api.common.authentication.CustomerAuthentication.Failure
import uk.co.baconi.oauth.api.common.authentication.CustomerAuthentication.Success
import uk.co.baconi.oauth.api.common.authentication.CustomerAuthenticationRequest
import kotlin.time.Duration.Companion.hours

interface AuthenticationRoute {

    val customerAuthenticationService: CustomerAuthenticationService

    fun Route.authentication() {

        application.log.info("Registering the AuthenticationRoute.authentication() routes")

        // TODO - Update error handling to always return JSON, even on 500's
        route("/authentication") {
            contentType(Application.Json) {
                post {
                    runCatching {
                        call.receive<CustomerAuthenticationRequest>()
                    }.onFailure { exception ->
                        application.log.debug("Bad CustomerAuthenticationRequest", exception)
                        call.respond(BadRequest, Failure())
                    }.onSuccess { request ->
                        when(val result = customerAuthenticationService.authenticate(request.username, request.password)) {
                            is Failure -> call.respond<CustomerAuthentication>(Unauthorized, result)
                            is Success -> {
                                // TODO - Replace with a JWT that we can then verify on the authorisation endpoint.
                                call.response.cookies.append(
                                    name = "Customer",
                                    value = result.username.value,
                                    maxAge = 4.hours.inWholeSeconds,
                                    //secure = true, // TODO - Enable if behind TLS, may need https://ktor.io/docs/forward-headers.html
                                    httpOnly = true,
                                )
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