package uk.co.baconi.oauth.common.authentication

import io.ktor.client.*

// TODO - Reconsider as it probably just wants to be in the specific module
expect fun createHttpClient(): HttpClient
