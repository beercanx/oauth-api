package uk.co.baconi.oauth.api.exchange.grants.refresh

import uk.co.baconi.oauth.api.exchange.ExchangeResponse
import uk.co.baconi.oauth.api.exchange.RefreshTokenRequest
import uk.co.baconi.oauth.api.exchange.grants.ConfidentialGrant

class RefreshGrant : ConfidentialGrant<RefreshTokenRequest> {

    override suspend fun exchange(request: RefreshTokenRequest): ExchangeResponse {
        TODO("Not yet implemented: $request")
    }
}