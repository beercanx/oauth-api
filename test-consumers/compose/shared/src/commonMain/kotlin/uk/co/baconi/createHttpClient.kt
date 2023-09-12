package uk.co.baconi

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.plus
import uk.co.baconi.session.sessionSerializersModule

fun createHttpClient(): HttpClient = HttpClient(getHttpClientEngine()) {
    expectSuccess = true
    followRedirects = false
    install(ContentNegotiation) {
        json(Json {
            serializersModule += sessionSerializersModule
        })
    }
}
