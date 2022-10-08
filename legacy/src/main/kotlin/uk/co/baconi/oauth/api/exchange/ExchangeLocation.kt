package uk.co.baconi.oauth.api.exchange

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("/token")
class ExchangeLocation