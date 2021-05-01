package uk.co.baconi.oauth.api.openid.exchange.flows

import uk.co.baconi.oauth.api.openid.exchange.ExchangeResponse
import uk.co.baconi.oauth.api.openid.exchange.ValidatedConfidentialExchangeRequest

interface ConfidentialGrant<A : ValidatedConfidentialExchangeRequest> {
    suspend fun exchange(request: A): ExchangeResponse
}