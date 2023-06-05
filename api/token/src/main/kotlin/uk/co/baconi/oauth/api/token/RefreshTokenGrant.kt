package uk.co.baconi.oauth.api.token

import uk.co.baconi.oauth.api.common.token.AccessTokenService
import uk.co.baconi.oauth.api.common.token.RefreshTokenService
import java.time.temporal.ChronoUnit.SECONDS

class RefreshTokenGrant(
    private val accessTokenService: AccessTokenService,
    private val refreshTokenService: RefreshTokenService
) {

    /**
     * If valid and authorized, the authorization server issues an access
     * token as described in Section 5.1.  If the request failed
     * verification or is invalid, the authorization server returns an error
     * response as described in Section 5.2.
     *
     * The authorization server MAY issue a new refresh token, in which case
     * the client MUST discard the old refresh token and replace it with the
     * new refresh token.  The authorization server MAY revoke the old
     * refresh token after issuing a new refresh token to the client.  If a
     * new refresh token is issued, the refresh token scope MUST be
     * identical to that of the refresh token included by the client in the
     * request.
     */
    fun exchange(request: RefreshTokenRequest): TokenResponse {

        val accessToken = accessTokenService.issue(
            username = request.refreshToken.username,
            clientId = request.refreshToken.clientId,
            scopes = request.scopes
        )

        val refreshToken = refreshTokenService.issue(
            username = request.refreshToken.username,
            clientId = request.refreshToken.clientId,
            scopes = request.refreshToken.scopes
        )

        // TODO - Revoke the old refresh token

        return TokenResponse.Success(
            accessToken = accessToken.value,
            refreshToken = refreshToken.value,
            expiresIn = SECONDS.between(accessToken.issuedAt, accessToken.expiresAt),
            scope = accessToken.scopes,
            state = null // TODO - Is this right for a token refresh?
        )
    }
}