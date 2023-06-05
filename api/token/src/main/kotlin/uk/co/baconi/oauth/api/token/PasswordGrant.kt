package uk.co.baconi.oauth.api.token

import uk.co.baconi.oauth.api.common.authentication.CustomerAuthentication
import uk.co.baconi.oauth.api.common.authentication.CustomerAuthenticationService
import uk.co.baconi.oauth.api.common.token.AccessTokenService
import uk.co.baconi.oauth.api.common.token.RefreshTokenService
import uk.co.baconi.oauth.api.token.TokenErrorType.InvalidGrant
import java.time.temporal.ChronoUnit.SECONDS

class PasswordGrant(
    private val accessTokenService: AccessTokenService,
    private val refreshTokenService: RefreshTokenService,
    private val authenticationService: CustomerAuthenticationService,
) {

    suspend fun exchange(request: PasswordRequest): TokenResponse {

        return when(val authentication = authenticationService.authenticate(request.username, request.password)) {
            // TODO - Reconsider the description value
            is CustomerAuthentication.Failure -> TokenResponse.Failed(InvalidGrant, "${authentication.reason}")
            is CustomerAuthentication.Success -> {

                val accessToken = accessTokenService.issue(
                    username = authentication.username,
                    clientId = request.principal.id,
                    scopes = request.scopes
                )

                val refreshToken = refreshTokenService.issue(
                    username = authentication.username,
                    clientId = request.principal.id,
                    scopes = request.scopes
                )

                TokenResponse.Success(
                    accessToken = accessToken.value,
                    refreshToken = refreshToken.value,
                    expiresIn = SECONDS.between(accessToken.issuedAt, accessToken.expiresAt),
                    scope = accessToken.scopes,
                    state = null
                )
            }
        }
    }
}