package uk.co.baconi.oauth.api.openid.exchange.flows.refresh

import uk.co.baconi.oauth.api.openid.exchange.ExchangeResponse
import uk.co.baconi.oauth.api.openid.exchange.RefreshTokenRequest
import uk.co.baconi.oauth.api.openid.exchange.flows.ConfidentialGrant

class RefreshGrant : ConfidentialGrant<RefreshTokenRequest> {

    override suspend fun exchange(request: RefreshTokenRequest): ExchangeResponse {
        TODO("Not yet implemented: $request")
    }
}