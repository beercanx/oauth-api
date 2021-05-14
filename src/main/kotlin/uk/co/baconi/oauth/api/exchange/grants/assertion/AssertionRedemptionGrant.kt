package uk.co.baconi.oauth.api.exchange.grants.assertion

import uk.co.baconi.oauth.api.exchange.AssertionRequest
import uk.co.baconi.oauth.api.exchange.ExchangeResponse
import uk.co.baconi.oauth.api.exchange.grants.ConfidentialGrant

// TODO - Verify name
class AssertionRedemptionGrant : ConfidentialGrant<AssertionRequest> {

    override suspend fun exchange(request: AssertionRequest): ExchangeResponse {
        TODO("Not yet implemented: $request")
    }
}