package uk.co.baconi.oauth.api.openid.exchange.flows.authorization

import uk.co.baconi.oauth.api.openid.exchange.AuthorizationCodeRequest
import uk.co.baconi.oauth.api.openid.exchange.ExchangeResponse
import uk.co.baconi.oauth.api.openid.exchange.PkceAuthorizationCodeRequest
import uk.co.baconi.oauth.api.openid.exchange.flows.ConfidentialFlow
import uk.co.baconi.oauth.api.openid.exchange.flows.PublicFlow

class AuthorizationCodeFlow : ConfidentialFlow<AuthorizationCodeRequest>, PublicFlow<PkceAuthorizationCodeRequest> {

    override suspend fun exchange(request: AuthorizationCodeRequest): ExchangeResponse {
        TODO("Not yet implemented: $request")
    }

    override suspend fun exchange(request: PkceAuthorizationCodeRequest): ExchangeResponse {
        TODO("Not yet implemented: $request")
    }
}