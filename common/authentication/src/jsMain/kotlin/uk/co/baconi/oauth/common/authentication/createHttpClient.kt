package uk.co.baconi.oauth.common.authentication

import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*

actual fun createHttpClient(): HttpClient = HttpClient(Js) {
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
}