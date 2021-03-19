package com.sbgcore.oauth.api.openid.exchange.flows.authorization

import com.sbgcore.oauth.api.openid.exchange.AuthorizationCodeRequest
import com.sbgcore.oauth.api.openid.exchange.ExchangeResponse
import com.sbgcore.oauth.api.openid.exchange.PkceAuthorizationCodeRequest
import com.sbgcore.oauth.api.openid.exchange.flows.ConfidentialFlow
import com.sbgcore.oauth.api.openid.exchange.flows.PublicFlow

class AuthorizationCodeFlow : ConfidentialFlow<AuthorizationCodeRequest>, PublicFlow<PkceAuthorizationCodeRequest> {

    override suspend fun exchange(request: AuthorizationCodeRequest): ExchangeResponse {
        TODO("Not yet implemented: $request")
    }

    override suspend fun exchange(request: PkceAuthorizationCodeRequest): ExchangeResponse {
        TODO("Not yet implemented: $request")
    }
}