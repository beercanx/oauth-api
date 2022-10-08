package uk.co.baconi.oauth.api.authentication

import io.ktor.http.ContentType.Application
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.http.HttpStatusCode.Companion.UnsupportedMediaType
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import uk.co.baconi.oauth.api.common.html.ReactTemplate.reactPage
import uk.co.baconi.oauth.api.common.authentication.CustomerAuthentication
import uk.co.baconi.oauth.api.common.authentication.CustomerAuthentication.Failure
import uk.co.baconi.oauth.api.common.authentication.CustomerAuthentication.Success
import uk.co.baconi.oauth.api.common.authentication.CustomerAuthenticationRequest
import uk.co.baconi.oauth.api.common.authentication.CustomerAuthenticationService
import java.util.*
import kotlin.time.Duration.Companion.hours

interface AuthenticationRoute {

    val customerAuthenticationService: CustomerAuthenticationService

    fun Route.authentication() {

        application.log.info("Registering the AuthenticationRoute.authentication() routes")

        // TODO - Update error handling to always return JSON, even on 500's
        route("/authentication") {
            contentType(Application.Json) {
                get {
                    // TODO - Setup return CSRF token and store it in say a pre authenticated session.
                    call.respondText(contentType = Application.Json) {
                        """{"csrfToken":"${UUID.randomUUID()}"}""" // TODO - Convert to an object and use serialisation
                    }
                }
                post {
                    // TODO - Expect CSRF token
                    runCatching {
                        call.receive<CustomerAuthenticationRequest>()
                    }.onFailure { exception ->
                        application.log.debug("Bad CustomerAuthenticationRequest", exception)
                        call.respond(BadRequest, Failure())
                    }.onSuccess { (username, password) ->
                        when (val result = customerAuthenticationService.authenticate(username, password)) {
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
            get {
                call.respondHtml(OK) {
                    reactPage(
                        title = "Login Page",
                        reactSource = "http://localhost:8080/static/js/bundle.js" // TODO - Extract into configuration
                    )
                }
            }
            post {
                call.respond(UnsupportedMediaType)
            }
        }
    }
}