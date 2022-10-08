package uk.co.baconi.oauth.api.token

import uk.co.baconi.oauth.api.common.authorisation.AuthorisationCode
import uk.co.baconi.oauth.api.common.client.ClientPrincipal

data class AuthorisationCodeRequest(
    override val principal: ClientPrincipal,
    val code: AuthorisationCode
) : TokenRequest.Valid<ClientPrincipal> {
    override fun toString(): String {
        return "AuthorisationCodeRequest(principal=$principal, code='REDACTED')"
    }
}