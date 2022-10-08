package uk.co.baconi.oauth.api.exchange.grants

import uk.co.baconi.oauth.api.exchange.ExchangeResponse
import uk.co.baconi.oauth.api.exchange.ValidPublicExchangeRequest

interface PublicGrant<A : ValidPublicExchangeRequest> {
    suspend fun exchange(request: A): ExchangeResponse
}