package uk.co.baconi.oauth.api.authentication

import io.ktor.application.*
import io.ktor.html.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.routing.*
import kotlinx.html.*
import uk.co.baconi.oauth.api.kotlinx.html.crossorigin
import java.util.*


interface AuthenticationRoute {

    // TODO - Consider if we need the pre-authenticated session to be created and destroyed by Authorization instead.

    // TODO - https://cheatsheetseries.owasp.org/cheatsheets/Cross-Site_Request_Forgery_Prevention_Cheat_Sheet.html#login-csrf
    // TODO - https://cheatsheetseries.owasp.org/cheatsheets/Cross-Site_Request_Forgery_Prevention_Cheat_Sheet.html#synchronizer-token-pattern

    fun Route.authentication() {

        get<AuthenticationLocation> {

            // TODO - Create pre-authenticated session
            // TODO - Create a base page template
            // TODO - Create a login page template

            // TODO - Add form validation (client-side)
            call.respondHtml {

                lang = "en"

                head {

                    meta(charset = "utf-8")
                    meta(name = "viewport", content = "width=device-width, initial-scale=1")

                    // https://getbootstrap.com/docs/5.0/getting-started/introduction/
                    link {
                        href = "https://cdn.jsdelivr.net/npm/bootstrap@5.0.1/dist/css/bootstrap.min.css"
                        rel = "stylesheet"
                        integrity = "sha384-+0n0xVW2eSR5OomGNYDnhzAbDsOXxcvSN1TPprVMTNDbiYZCxYbOOl7+AMvyTG2x"
                        crossorigin = "anonymous"
                    }

                    title {
                        +"Authentication"
                    }
                }
                body {
                    div(classes = "container") {

                        postForm(action = href(AuthenticationLocation)) {
                            id = "login-form"

                            hiddenInput {
                                name = "csrf_token"
                                value = UUID.randomUUID().toString() // TODO - Store in pre-authenticated session
                            }

                            h1(classes = "text-center") {
                                +"Authentication"
                            }

                            div(classes = "mb-3") {
                                label(classes = "form-label") {
                                    htmlFor = "username"
                                    text("Username: ")
                                }
                                textInput(classes = "form-control") { // TODO - Convert from username to email, as its bad UX or at least allow email login as well.
                                    name = "username"
                                    placeholder = "Enter username"
                                    autoComplete = true
                                }
                            }

                            div(classes = "mb-3") {
                                label(classes = "form-label") {
                                    htmlFor = "password"
                                    text("Password: ")
                                }
                                passwordInput(classes = "form-control") {
                                    name = "password"
                                    placeholder = "Password"
                                    autoComplete = true
                                }
                            }

                            button(classes = "btn btn-primary w-100") {
                                name = "login"
                                type = ButtonType.submit
                                text("Login")
                            }
                        }
                    }

                    script {
                        src = "https://cdn.jsdelivr.net/npm/bootstrap@5.0.1/dist/js/bootstrap.bundle.min.js"
                        integrity = "sha384-gtEjrD/SeCtmISkJkNUaaKMoLD0//ElJ19smozuHV6z3Iehds+3Ulb9Bn9Plx0x4"
                        crossorigin = "anonymous"
                    }
                }
            }
        }

        post<AuthenticationLocation> {

            // TODO - Handle login form submission
            // TODO - Check csrf_token before processing anything

            // TODO - Add form validation (server side)
            //          Respond 400?
            //          Respond with html form filled in and validation messages shown?
            //          How do we tied both client and server side validation?

            val parameters = call.receiveParameters()
            application.log.trace("${call.request.local.uri} with: $parameters")

            // TODO - On successful authentication
            // TODO - Create full session.
            // TODO - Destroy pre-authenticated session.

            call.respondHtml {
                head {
                    title {
                        +"Authentication - Posted"
                    }
                }
                body {
                    p {
                        +"csrf_token: ${parameters["csrf_token"]}"
                    }
                    p {
                        +"username: ${parameters["username"]}"
                    }
                    p {
                        +"password: <REDACTED>"
                    }
                }
            }
        }
    }
}