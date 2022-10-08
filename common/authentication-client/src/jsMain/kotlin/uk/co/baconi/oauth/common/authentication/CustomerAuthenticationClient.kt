package uk.co.baconi.oauth.common.authentication

import io.ktor.http.*
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

import org.w3c.fetch.Headers
import org.w3c.fetch.INCLUDE
import org.w3c.fetch.RequestCredentials
import org.w3c.fetch.RequestInit

// TODO - Move into user-interface:authentication and rename this module authentication-common?
class CustomerAuthenticationClient(private val authenticationEndpoint: String = "http://localhost:8080/authentication") {

    companion object {
        private val json = Json
    }

    // TODO - Improve error handling/capture
    suspend fun authenticate(username: String, password: CharArray): CustomerAuthentication {

        // TODO - Clear password CharArray?

        // Using fetch so that we can take advantage of `credentials: include` to preserve HTTP Only cookies for auth.
        return window
            .fetch(
                authenticationEndpoint, RequestInit(
                    method = HttpMethod.Post.value,
                    headers = Headers().contentType(ContentType.Application.Json),
                    credentials = RequestCredentials.INCLUDE,
                    body = json.encodeToString(CustomerAuthenticationRequest(username, password))
                )
            )
            .await()
            .text()
            .await()
            .let { body -> json.decodeFromString(body) }
    }

    private fun Headers.contentType(contentType: ContentType): Headers = apply {
        append(HttpHeaders.ContentType, contentType.toString())
    }
}