package uk.co.baconi.session

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.http.ContentType.Application.FormUrlEncoded
import io.ktor.http.ContentType.Application.Json
import uk.co.baconi.createHttpClient
import uk.co.baconi.getLocalhost

class SessionService(
    private val httpClient: HttpClient = createHttpClient()
) {

    private val tokenEndpoint: String = "http://${getLocalhost()}:8080/token"
    private val clientId = "consumer-z"
    private val clientSecret = "7XLlyzjRpvICEkNrsgtOuuj1S30Bj9Xu"

    suspend fun getSession(username: String, password: String): Session {

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

        val result = response.body<Success>()
        println(result)
        return result
    }

}