package uk.co.baconi.oauth.api.exchange.grants.refresh

import uk.co.baconi.oauth.api.exchange.ExchangeResponse
import uk.co.baconi.oauth.api.exchange.RefreshTokenRequest
import uk.co.baconi.oauth.api.exchange.grants.ConfidentialGrant

class RefreshGrant : ConfidentialGrant<RefreshTokenRequest> {

    // If 'scope' is provided in the request,
    //      it cannot contain more than what was already issued,
    //      but can be less to reduce the scope of the access token issued.

    // if 'scope' is omitted then treat as equal scope as originally granted.

    // refresh token can only be redeemed by the client that it was issued to.

    // The authorization server MAY issue a new refresh token, in which case
    //   the client MUST discard the old refresh token and replace it with the
    //   new refresh token.

    // The authorization server MAY revoke the old
    //   refresh token after issuing a new refresh token to the client.

    // If a new refresh token is issued, the refresh token scope MUST be
    //   identical to that of the refresh token included by the client in the
    //   request.

    // NOTE: 'scope' is only for changing the access tokens scope, refresh token still keeps its original scope.

    override suspend fun exchange(request: RefreshTokenRequest): ExchangeResponse {
        TODO("Not yet implemented: $request")
    }
}