package uk.co.baconi.oauth.common.authentication

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class CustomerAuthenticationClient(
    private val httpClient: HttpClient = createHttpClient(),
    private val baseUrl: String = "http://localhost:8080",
    private val authenticationEndpoint: String = "$baseUrl/authentication",
) {

    // TODO - Do we need any pre-data to setup safe login form submission (think owasp recommendations)

    suspend fun authenticate(
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