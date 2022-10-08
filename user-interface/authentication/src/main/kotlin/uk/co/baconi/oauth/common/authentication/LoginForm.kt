package uk.co.baconi.oauth.common.authentication

import csstype.ClassName
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLFormElement
import react.FC
import react.Props
import react.dom.events.FormEventHandler
import react.dom.html.AutoComplete
import react.dom.html.ButtonType
import react.dom.html.InputType
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.form
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.label
import react.useState

external interface LoginFormProps : Props {
    var authenticationEndpoint: String
    var csrfToken: String // TODO - Do with need CSRF?
}

val mainScope = MainScope()

// TODO - Look at ES6 Class Components...
val LoginForm = FC<LoginFormProps> { props ->

    // TODO - Verify if this is the right way to go about this
    var username: String by useState("")
    var password: String by useState("")

    val authenticationClient = CustomerAuthenticationClient(authenticationEndpoint = props.authenticationEndpoint)

    val handleSubmit: FormEventHandler<HTMLFormElement> = { event ->
        event.preventDefault()
        // TODO - Handle abort
        // TODO - Handle redirect on success
        // TODO - Prevent multiple submissions
        mainScope.launch {
            console.log("Submitted!")
            val result = authenticationClient.authenticate(username, password.toCharArray())
            console.log("Deserialized:", result)
        }
    }

    h1 {
        className = ClassName("text-center")
        +"Authentication"
    }

    form {
        id = "login-form"
        onSubmit = handleSubmit

        input {
            type = InputType.hidden
            name = "csrf_token"
            value = props.csrfToken
        }

        // TODO - insert(beforeInput) - Implement failure messages

        div {
            className = ClassName("mb-3")

            label {
                className = ClassName("form-label")
                htmlFor = "username"
                +"Username"
            }
            input {
                className = ClassName("form-control")
                type = InputType.text
                name = "username"
                placeholder = "Enter username"
                autoComplete = AutoComplete.on
                onChange = { event ->
                    username = event.target.value
                }
                //+username
            }
        }

        div {
            className = ClassName("mb-3")

            label {
                className = ClassName("form-label")
                htmlFor = "password"
                +"Password"
            }
            input {
                className = ClassName("form-control")

                type = InputType.password
                name = "password"
                placeholder = "Password"
                autoComplete = AutoComplete.on
                onChange = { event ->
                    password = event.target.value
                }
                //+password
            }
        }

        div {
            className = ClassName("row")

            div {
                className = ClassName("col-sm")

                button {
                    className = ClassName("btn btn-primary w-100")
                    name = "login"
                    value = "login"
                    type = ButtonType.submit
                    +"Login"
                }
            }

            div {
                className = ClassName("col-sm")

                button {
                    className = ClassName("btn btn-secondary w-100")
                    name = "abort"
                    value = "abort"
                    type = ButtonType.submit
                    +"Abort"
                }
            }
        }
    }
}