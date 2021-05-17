package uk.co.baconi.oauth.api.authentication

import io.ktor.html.*
import io.ktor.http.*
import io.ktor.locations.*
import kotlinx.html.*
import uk.co.baconi.oauth.api.kotlinx.html.PageTemplate
import java.util.*

class AuthenticationPageTemplate(private val locations: Locations) : Template<HTML> {

    companion object {

        const val CSRF_TOKEN = "csrf_token"
        const val USERNAME = "username"
        const val PASSWORD = "password"

        private fun INPUT.prefill(parameters: Parameters) {
            value = parameters[name] ?: ""
        }

        private fun INPUT.applyValidation(parameters: Parameters) {
            // TODO - Add accessibility describe tag when invalid.
            // TODO - Include invalid-feedback element when invalid.
            classes = classes + if(parameters[name].isNullOrBlank()) {
                "is-invalid"
            } else {
                "is-valid"
            }
        }
    }

    private val csrfToken = Placeholder<INPUT>()
    private val username = Placeholder<INPUT>()
    private val password = Placeholder<INPUT>()

    fun csrfToken(token: UUID) {
        csrfToken { value = token.toString() }
    }

    fun prefill(parameters: Parameters) {
        username {
            prefill(parameters)
            applyValidation(parameters)
        }
        password {
            prefill(parameters)
            applyValidation(parameters)
        }
    }

    override fun HTML.apply() {

        insert(PageTemplate()) {

            pageTitle {
                +"Authentication"
            }

            pageContent {

                h1(classes = "text-center") {
                    +"Authentication"
                }

                postForm {
                    id = "login-form"
                    action = locations.href(AuthenticationLocation)

                    hiddenInput {
                        name = CSRF_TOKEN
                        insert(csrfToken)
                    }

                    div(classes = "mb-3") {
                        label(classes = "form-label") {
                            htmlFor = "username"
                            text("Username")
                        }
                        textInput(classes = "form-control") { // TODO - Convert from username to email, as its bad UX or at least allow email login as well.
                            name = USERNAME
                            placeholder = "Enter username"
                            autoComplete = true
                            insert(username)
                        }
                        div("invalid-feedback") { // TODO - Don't render when input was valid
                            +"Please provide a username."
                        }
                    }

                    div(classes = "mb-3") {
                        label(classes = "form-label") {
                            htmlFor = "password"
                            text("Password")
                        }
                        passwordInput(classes = "form-control") {
                            name = PASSWORD
                            placeholder = "Password"
                            autoComplete = true
                            insert(password)
                        }
                        div("invalid-feedback") { // TODO - Don't render when input was valid
                            +"Please provide a password."
                        }
                    }

                    button(classes = "btn btn-primary w-100") {
                        name = "login"
                        value = "login"
                        type = ButtonType.submit
                        text("Login")
                    }
                }
            }
        }
    }
}