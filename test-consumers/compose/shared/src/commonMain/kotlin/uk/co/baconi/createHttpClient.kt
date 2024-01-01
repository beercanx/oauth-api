package uk.co.baconi

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*

fun createHttpClient(): HttpClient = HttpClient(getHttpClientEngine()) {
    expectSuccess = true
    followRedirects = false
    install(ContentNegotiation) {
        json()
    }
}
