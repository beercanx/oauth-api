package uk.co.baconi.oauth.api.exchange.grants.authorization

import uk.co.baconi.oauth.api.exchange.AuthorizationCodeRequest
import uk.co.baconi.oauth.api.exchange.ExchangeResponse
import uk.co.baconi.oauth.api.exchange.PkceAuthorizationCodeRequest
import uk.co.baconi.oauth.api.exchange.grants.ConfidentialGrant
import uk.co.baconi.oauth.api.exchange.grants.PublicGrant

class AuthorizationCodeGrant : ConfidentialGrant<AuthorizationCodeRequest>, PublicGrant<PkceAuthorizationCodeRequest> {

    override suspend fun exchange(request: AuthorizationCodeRequest): ExchangeResponse {
        TODO("Not yet implemented: $request")
    }

    override suspend fun exchange(request: PkceAuthorizationCodeRequest): ExchangeResponse {
        TODO("Not yet implemented: $request")
    }
}