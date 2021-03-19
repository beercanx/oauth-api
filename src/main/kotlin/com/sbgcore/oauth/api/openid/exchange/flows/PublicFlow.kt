package com.sbgcore.oauth.api.openid.exchange.flows

import com.sbgcore.oauth.api.openid.exchange.ExchangeResponse
import com.sbgcore.oauth.api.openid.exchange.ValidatedConfidentialExchangeRequest
import com.sbgcore.oauth.api.openid.exchange.ValidatedPublicExchangeRequest

interface PublicFlow<A : ValidatedPublicExchangeRequest> {
    fun exchange(request: A): ExchangeResponse
}