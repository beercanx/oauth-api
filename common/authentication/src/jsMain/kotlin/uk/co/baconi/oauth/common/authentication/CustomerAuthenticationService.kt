package uk.co.baconi.oauth.common.authentication

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*

actual class CustomerAuthenticationService(
    private val httpClient: HttpClient = HttpClient(Js) {
        followRedirects = false
        install(ContentNegotiation) {
            json()
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 1_000
            connectTimeoutMillis = 200 // TODO - Review in context of being in the browser to the server
        }
        install(HttpRequestRetry) {
            // Fairly sure this only retires communication and not on serialisation issues.
            retryOnExceptionOrServerErrors(maxRetries = 5)
        }
    },
    private val baseUrl: String = "http://localhost:8080",
    private val authenticationEndpoint: String = "$baseUrl/authentication"
) {

    // TODO - Do we need any pre-data to setup safe login form submission (think owasp recommendations)

    actual suspend fun authenticate(
        username: String,
        password: CharArray
    ): CustomerAuthentication {

        val response = httpClient.post(authenticationEndpoint) {
            contentType(ContentType.Application.Json)
            setBody(CustomerAuthenticationRequest(username = username, password = password))
        }

        return response.body()
    }
}