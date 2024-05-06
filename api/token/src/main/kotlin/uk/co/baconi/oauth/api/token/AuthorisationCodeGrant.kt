package uk.co.baconi.oauth.api.token

import uk.co.baconi.oauth.api.common.authorisation.AuthorisationCodeRepository
import uk.co.baconi.oauth.api.common.token.AccessTokenService
import uk.co.baconi.oauth.api.common.token.RefreshTokenService
import java.time.temporal.ChronoUnit.SECONDS

class AuthorisationCodeGrant(
    private val accessTokenService: AccessTokenService,
    private val refreshTokenService: RefreshTokenService,
    private val authorisationCodeRepository: AuthorisationCodeRepository,
) {

    fun exchange(request: AuthorisationCodeRequest): TokenResponse {

        // Invalidate the authorisation code, it's a single use token.
        authorisationCodeRepository.markUsed(request.code)

        val accessToken = accessTokenService.issue(
            username = request.code.username,
            clientId = request.code.clientId,
            scopes = request.code.scopes
        )

        val refreshToken = refreshTokenService.issue(
            username = request.code.username,
            clientId = request.code.clientId,
            scopes = request.code.scopes
        )

        return TokenResponse.Success(
            accessToken = accessToken.value,
            refreshToken = refreshToken.value,
            expiresIn = SECONDS.between(accessToken.issuedAt, accessToken.expiresAt),
            scope = accessToken.scopes,
            state = request.code.state
        )
    }
}