package uk.co.baconi.oauth.common.authentication

import kotlinx.browser.window

import org.w3c.fetch.Headers
import org.w3c.fetch.INCLUDE
import org.w3c.fetch.RequestCredentials
import org.w3c.fetch.RequestInit
import kotlin.js.Promise
import kotlin.js.json

class CustomerAuthenticationClient(private val authenticationEndpoint: String = "http://localhost:8080/authentication") {

    // TODO - Improve error handling/capture
    fun authenticate(username: String, password: String): Promise<CustomerAuthentication> {

        // Using fetch so that we can take advantage of `credentials: include` to preserve HTTP Only cookies for auth.
        return window
            .fetch(
                authenticationEndpoint, RequestInit(
                    method = "POST",
                    headers = Headers().contentType("application/json"),
                    credentials = RequestCredentials.INCLUDE,
                    body = JSON.stringify(json(
                        "username" to username,
                        "password" to password.map { c -> "$c" }
                    ))
                )
            )
            .then { result -> result.text() }
            .then { body -> JSON.parse<CustomerAuthentication>(body) }
            .then { response ->
                response.also { checkNotNull(it.type) { "Response is missing response type marker." } }
            }
    }

    private fun Headers.contentType(contentType: String): Headers = apply {
        append("Content-Type", contentType)
    }
}