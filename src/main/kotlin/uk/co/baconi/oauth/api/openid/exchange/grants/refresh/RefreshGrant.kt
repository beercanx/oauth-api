package uk.co.baconi.oauth.api.openid.exchange.grants.refresh

import uk.co.baconi.oauth.api.openid.exchange.ExchangeResponse
import uk.co.baconi.oauth.api.openid.exchange.RefreshTokenRequest
import uk.co.baconi.oauth.api.openid.exchange.grants.ConfidentialGrant

class RefreshGrant : ConfidentialGrant<RefreshTokenRequest> {

    override suspend fun exchange(request: RefreshTokenRequest): ExchangeResponse {
        TODO("Not yet implemented: $request")
    }
}