package uk.co.baconi.oauth.api.authentication

import io.ktor.html.*
import io.ktor.locations.*
import kotlinx.html.*
import uk.co.baconi.oauth.api.kotlinx.html.PageTemplate

class AuthenticationPageTemplate(private val locations: Locations) : Template<HTML> {

    companion object {
        const val CSRF_TOKEN = "csrf_token"
        const val USERNAME = "username"
        const val PASSWORD = "password"
    }

    val csrfToken = Placeholder<INPUT>()
    val username = Placeholder<INPUT>()
    val password = Placeholder<INPUT>()
    val beforeInput = Placeholder<FORM>()

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

                    insert(beforeInput)

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
//                        div("invalid-feedback") { // TODO - Don't render when input was valid
//                            +"Please provide a username."
//                        }
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
//                        div("invalid-feedback") { // TODO - Don't render when input was valid
//                            +"Please provide a password."
//                        }
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