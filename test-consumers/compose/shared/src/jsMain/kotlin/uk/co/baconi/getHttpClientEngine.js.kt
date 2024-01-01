package uk.co.baconi

import io.ktor.client.engine.*
import io.ktor.client.engine.js.*

actual fun getHttpClientEngine(): HttpClientEngineFactory<*> = Js
