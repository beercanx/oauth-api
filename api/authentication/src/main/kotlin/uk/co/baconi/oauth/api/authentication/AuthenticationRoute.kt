package uk.co.baconi.oauth.api.authentication

import io.ktor.http.ContentType.Application
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import uk.co.baconi.oauth.common.authentication.CustomerAuthentication
import uk.co.baconi.oauth.common.authentication.CustomerAuthentication.Failure
import uk.co.baconi.oauth.common.authentication.CustomerAuthentication.Success
import uk.co.baconi.oauth.common.authentication.CustomerAuthenticationRequest
import uk.co.baconi.oauth.common.authentication.CustomerAuthenticationService

interface AuthenticationRoute {

    val customerAuthenticationService: CustomerAuthenticationService

    fun Route.authentication() {

        application.log.info("Registering the AuthenticationRoute.authentication() routes")

        // TODO - Verify MethodNotAllowed
        // TODO - Verify UnsupportedMediaType
        route("/authentication") {
            contentType(Application.Json) {
                post {
                    runCatching {
                        call.receive<CustomerAuthenticationRequest>()
                    }.onFailure { exception ->
                        application.log.debug("Bad CustomerAuthenticationRequest", exception)
                        call.respond(BadRequest)
                    }.onSuccess { request ->
                        when(val result = customerAuthenticationService.authenticate(request.username, request.password)) {
                            is Failure -> call.respond<CustomerAuthentication>(Unauthorized, result)
                            is Success -> call.respond<CustomerAuthentication>(OK, result)
                        }
                    }
                }
            }
        }
    }
}