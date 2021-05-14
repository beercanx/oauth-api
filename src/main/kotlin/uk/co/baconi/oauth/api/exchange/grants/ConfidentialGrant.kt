package uk.co.baconi.oauth.api.exchange.grants

import uk.co.baconi.oauth.api.exchange.ExchangeResponse
import uk.co.baconi.oauth.api.exchange.ValidatedConfidentialExchangeRequest

interface ConfidentialGrant<A : ValidatedConfidentialExchangeRequest> {
    suspend fun exchange(request: A): ExchangeResponse
}