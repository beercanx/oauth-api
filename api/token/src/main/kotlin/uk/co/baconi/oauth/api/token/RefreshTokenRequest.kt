package uk.co.baconi.oauth.api.token

import uk.co.baconi.oauth.api.common.client.ClientPrincipal
import uk.co.baconi.oauth.api.common.scope.Scope
import uk.co.baconi.oauth.api.common.token.RefreshToken

data class RefreshTokenRequest(
    override val principal: ClientPrincipal,
    val scopes: Set<Scope>,
    val refreshToken: RefreshToken
) : TokenRequest.Valid<ClientPrincipal> {
    override fun toString(): String {
        return "RefreshTokenRequest(principal=$principal, scopes=$scopes, refreshToken='REDACTED')"
    }
}