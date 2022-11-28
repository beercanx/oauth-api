package uk.co.baconi.oauth.api.authentication

import io.ktor.http.*
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
import io.ktor.server.util.*
import uk.co.baconi.oauth.api.common.html.ReactTemplate.reactPage
import uk.co.baconi.oauth.api.common.authentication.CustomerAuthentication
import uk.co.baconi.oauth.api.common.authentication.CustomerAuthentication.Failure
import uk.co.baconi.oauth.api.common.authentication.CustomerAuthentication.Success
import uk.co.baconi.oauth.api.common.authentication.CustomerAuthenticationRequest
import uk.co.baconi.oauth.api.common.authentication.CustomerAuthenticationService
import uk.co.baconi.oauth.api.common.location.Location
import java.util.*
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

private const val COOKIE_CSRF = "Authenticate-CSRF"
private const val COOKIE_CUSTOMER = "Authenticated-Customer"

interface AuthenticationRoute {

    val customerAuthenticationService: CustomerAuthenticationService

    fun Route.authentication() {

        application.log.info("Registering the AuthenticationRoute.authentication() routes")

        val bundleLocation = url {
            takeFrom(Location.Assets.baseUrl)
            path("/assets/js/authentication.js") // TODO - Extract into configuration
        }

        application.log.debug("Authentication location: $bundleLocation")

        // TODO - Update error handling to always return JSON, even on 500's
        route("/authentication") {
            contentType(Application.Json) {
                get {

                    val csrfToken = UUID.randomUUID()

                    // TODO - Convert to signed client session or switch to passed JWT.
                    call.response.cookies.append(
                        name = COOKIE_CSRF,
                        value = csrfToken.toString(),
                        maxAge = 30.minutes.inWholeSeconds,
                        //secure = true, // TODO - Enable if behind TLS, may need https://ktor.io/docs/forward-headers.html
                        httpOnly = true,
                    )

                    call.respond(OK, CsrfToken(csrfToken)) // TODO - Verify this is JSON shaped
                }
                post {
                    // TODO - Expect CSRF token
                    runCatching {
                        val request = call.receive<CustomerAuthenticationRequest>()
                        val expectedCsrfToken = call.request.cookies[COOKIE_CSRF] // TODO - Verify HTTP only?
                        when {
                            expectedCsrfToken == null -> throw Exception("Invalid CSRF Token!")
                            request.csrfToken != expectedCsrfToken -> throw Exception("Invalid CSRF Token!")
                            else -> request
                        }
                    }.onFailure { exception ->
                        application.log.debug("Bad CustomerAuthenticationRequest", exception)
                        call.respond<CustomerAuthentication>(BadRequest, Failure())
                    }.onSuccess { (username, password) ->
                        when (val result = customerAuthenticationService.authenticate(username, password)) {
                            is Failure -> call.respond<CustomerAuthentication>(Unauthorized, result)
                            is Success -> {

                                // TODO - Convert to signed client session or switch to passed JWT.
                                call.response.cookies.append(
                                    name = COOKIE_CUSTOMER,
                                    value = result.username.value,
                                    maxAge = 24.hours.inWholeSeconds,
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
                // TODO - Create CSRF Token here and save into session, possibly use a short lived cached value?
                // TODO - Handle already being logged in, do we redirect, pop out a new UI or assume you're logging in as someone new?
                call.respondHtml(OK) {
                    reactPage(title = "Login Page", reactSource = bundleLocation)
                }
            }
            post {
                call.respond(UnsupportedMediaType)
            }
        }
    }
}