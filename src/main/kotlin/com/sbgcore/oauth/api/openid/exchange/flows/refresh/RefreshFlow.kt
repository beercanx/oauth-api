package com.sbgcore.oauth.api.openid.exchange.flows.refresh

import com.sbgcore.oauth.api.openid.exchange.ExchangeResponse
import com.sbgcore.oauth.api.openid.exchange.RefreshTokenRequest
import com.sbgcore.oauth.api.openid.exchange.flows.ConfidentialFlow

class RefreshFlow : ConfidentialFlow<RefreshTokenRequest> {

    override fun exchange(request: RefreshTokenRequest): ExchangeResponse {
        TODO("Not yet implemented: $request")
    }
}