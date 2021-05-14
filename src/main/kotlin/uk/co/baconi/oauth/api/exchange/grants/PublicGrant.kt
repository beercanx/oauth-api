package uk.co.baconi.oauth.api.exchange.grants

import uk.co.baconi.oauth.api.exchange.ExchangeResponse
import uk.co.baconi.oauth.api.exchange.ValidatedPublicExchangeRequest

interface PublicGrant<A : ValidatedPublicExchangeRequest> {
    suspend fun exchange(request: A): ExchangeResponse
}