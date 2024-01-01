package uk.co.baconi

import io.ktor.client.engine.*

expect fun getHttpClientEngine(): HttpClientEngineFactory<*>