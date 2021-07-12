package uk.co.baconi.oauth.api.exchange.grants

import uk.co.baconi.oauth.api.exchange.ExchangeResponse
import uk.co.baconi.oauth.api.exchange.ValidConfidentialExchangeRequest

interface ConfidentialGrant<A : ValidConfidentialExchangeRequest> {
    suspend fun exchange(request: A): ExchangeResponse
}