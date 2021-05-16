package uk.co.baconi.oauth.api.authentication

import io.ktor.html.*
import io.ktor.http.*
import io.ktor.locations.*
import kotlinx.html.*
import uk.co.baconi.oauth.api.kotlinx.html.PageTemplate
import java.util.*

class AuthenticationPageTemplate(private val locations: Locations) : Template<HTML> {

    companion object {

        fun AuthenticationPageTemplate.csrfToken(token: UUID) {
            csrfToken { value = token.toString() }
        }

        fun AuthenticationPageTemplate.prefill(parameters: Parameters) {
            username { value = parameters[name] ?: "" }
            password { value = parameters[name] ?: "" }
        }
    }

    private val csrfToken = Placeholder<INPUT>()
    private val username = Placeholder<INPUT>()
    private val password = Placeholder<INPUT>()

    override fun HTML.apply() {

        insert(PageTemplate()) {

            pageTitle {
                +"Authentication"
            }

            pageContent {

                h1(classes = "text-center") {
                    +"Authentication"
                }

                postForm(action = locations.href(AuthenticationLocation)) {
                    id = "login-form"

                    hiddenInput {
                        name = "csrf_token"
                        insert(csrfToken)
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
                            insert(username)
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
                            insert(password)
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