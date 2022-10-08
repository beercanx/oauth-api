package uk.co.baconi.oauth.common.authentication

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.js.*
import io.ktor.client.request.*
import io.ktor.http.*

actual class CustomerAuthenticationService(
    private val httpClient: HttpClient = HttpClient(Js),
    private val baseUrl: String = "http://localhost:8080",
    private val authenticationEndpoint: String = "$baseUrl/authentication"
) {

    // TODO - Do we need any pre-data to setup safe login form submission (think owasp recommendations)

    actual suspend fun authenticate(
        username: String,
        password: String
    ): CustomerAuthentication {

        val response = httpClient.post(authenticationEndpoint) {
            contentType(ContentType.Application.Json)
            setBody(CustomerAuthenticationRequest(username = username, password = password))
        }

        return response.body()
    }
}