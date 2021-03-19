package com.sbgcore.oauth.api.openid.exchange.flows.password

import com.sbgcore.oauth.api.openid.exchange.ExchangeResponse
import com.sbgcore.oauth.api.openid.exchange.PasswordRequest
import com.sbgcore.oauth.api.openid.exchange.flows.ConfidentialFlow

class PasswordFlow : ConfidentialFlow<PasswordRequest> {

    override fun exchange(request: PasswordRequest): ExchangeResponse {
        TODO("Not yet implemented: $request")
    }
}