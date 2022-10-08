package uk.co.baconi.oauth.api.authentication

import io.ktor.resources.serialization.*
import io.ktor.server.html.*
import kotlinx.html.*
import uk.co.baconi.oauth.api.kotlinx.html.PageTemplate
import uk.co.baconi.oauth.api.ktor.href

class AuthenticationPageTemplate(private val resourcesFormat: ResourcesFormat, private val location: AuthenticationLocation) :
    Template<HTML> {

    companion object {
        const val CSRF_TOKEN = "csrf_token"
        const val USERNAME = "username"
        const val PASSWORD = "password"
        const val LOGIN = "login"
        const val ABORT = "abort"
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
                    action = resourcesFormat.href(location)

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
                    }

                    div(classes = "row") {

                        div(classes = "col-sm") {
                            button(classes = "btn btn-primary w-100") {
                                name = LOGIN
                                value = "login"
                                type = ButtonType.submit
                                text("Login")
                            }
                        }

                        div(classes = "col-sm") {
                            button(classes = "btn btn-secondary w-100") {
                                name = ABORT
                                value = "abort"
                                type = ButtonType.submit
                                text("Abort")
                            }
                        }
                    }
                }
            }
        }
    }
}