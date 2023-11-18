package uk.co.baconi

import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*

actual fun getHttpClientEngine(): HttpClientEngineFactory<*> = CIO
