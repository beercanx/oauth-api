package com.sbgcore.oauth.api.openid.exchange.flows.assertion

import com.sbgcore.oauth.api.openid.exchange.AssertionRequest
import com.sbgcore.oauth.api.openid.exchange.ExchangeResponse
import com.sbgcore.oauth.api.openid.exchange.flows.ConfidentialFlow

// TODO - Verify name
class AssertionRedemptionFlow : ConfidentialFlow<AssertionRequest> {

    override fun exchange(request: AssertionRequest): ExchangeResponse {
        TODO("Not yet implemented: $request")
    }
}