package uk.co.baconi.session

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.http.ContentType.Application.FormUrlEncoded
import io.ktor.http.ContentType.Application.Json

class SessionService(
    private val httpClient: HttpClient,
    private val tokenEndpoint: String = "http://localhost:8080/token"
) {

    private val clientId = "consumer-z"
    private val clientSecret = "7XLlyzjRpvICEkNrsgtOuuj1S30Bj9Xu"

    suspend fun getSession(username: String, password: String) {

        // TODO - Replace with the authorisation code grant
        val response = httpClient.post(tokenEndpoint) {
            accept(Json)
            contentType(FormUrlEncoded)
            basicAuth(clientId, clientSecret)
            setBody(FormDataContent(parameters {
                append("grant_type", "password")
                append("username", username)
                append("password", password)
                append("scope", "basic")
            }))
        }

        when(val result = response.body<TokenResponse>()) {
            is Success -> println("Success: $result")
            is Failed -> println("Failure: $result")
        }
    }

}