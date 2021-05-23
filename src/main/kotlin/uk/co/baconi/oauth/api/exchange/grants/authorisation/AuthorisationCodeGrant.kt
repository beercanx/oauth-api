package uk.co.baconi.oauth.api.exchange.grants.authorisation

import uk.co.baconi.oauth.api.authorisation.AuthorisationCodeRepository
import uk.co.baconi.oauth.api.exchange.AuthorisationCodeRequest
import uk.co.baconi.oauth.api.exchange.ExchangeResponse
import uk.co.baconi.oauth.api.exchange.PkceAuthorisationCodeRequest
import uk.co.baconi.oauth.api.exchange.grants.ConfidentialGrant
import uk.co.baconi.oauth.api.exchange.grants.PublicGrant

class AuthorisationCodeGrant(
    private val authorisationCodeRepository: AuthorisationCodeRepository
) : ConfidentialGrant<AuthorisationCodeRequest>, PublicGrant<PkceAuthorisationCodeRequest> {

    override suspend fun exchange(request: AuthorisationCodeRequest): ExchangeResponse {
        TODO("Not yet implemented: $request")
    }

    override suspend fun exchange(request: PkceAuthorisationCodeRequest): ExchangeResponse {
        TODO("Not yet implemented: $request")
    }
}