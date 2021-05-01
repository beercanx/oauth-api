package uk.co.baconi.oauth.api.openid.exchange.grants.assertion

import uk.co.baconi.oauth.api.openid.exchange.AssertionRequest
import uk.co.baconi.oauth.api.openid.exchange.ExchangeResponse
import uk.co.baconi.oauth.api.openid.exchange.grants.ConfidentialGrant

// TODO - Verify name
class AssertionRedemptionGrant : ConfidentialGrant<AssertionRequest> {

    override suspend fun exchange(request: AssertionRequest): ExchangeResponse {
        TODO("Not yet implemented: $request")
    }
}