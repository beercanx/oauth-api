package uk.co.baconi

import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.*

actual fun getHttpClientEngine(): HttpClientEngineFactory<*> = OkHttp
