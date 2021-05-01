package uk.co.baconi.oauth.api.openid.exchange.flows

import uk.co.baconi.oauth.api.openid.exchange.ExchangeResponse
import uk.co.baconi.oauth.api.openid.exchange.ValidatedConfidentialExchangeRequest

interface ConfidentialFlow<A : ValidatedConfidentialExchangeRequest> {
    suspend fun exchange(request: A): ExchangeResponse
}