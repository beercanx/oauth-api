package uk.co.baconi.oauth.api.token

import uk.co.baconi.oauth.api.common.authentication.AuthenticatedUsername
import uk.co.baconi.oauth.api.common.token.AccessTokenService
import uk.co.baconi.oauth.api.common.token.RefreshTokenService
import java.time.temporal.ChronoUnit.SECONDS
import java.util.*

class AssertionGrant(
    private val accessTokenService: AccessTokenService,
    private val refreshTokenService: RefreshTokenService
) {

    fun exchange(request: AssertionRequest): TokenResponse {

        val accessToken = accessTokenService.issue(
            username = AuthenticatedUsername(UUID.randomUUID().toString()), // TODO - request.assertion.username,
            clientId = request.principal.id,
            scopes = emptySet() // TODO - request.assertion.scopes
        )

        val refreshToken = refreshTokenService.issue(
            username = AuthenticatedUsername(UUID.randomUUID().toString()), // TODO - request.assertion.username,
            clientId = request.principal.id,
            scopes = emptySet() // TODO - request.assertion.scopes
        )

        return TokenResponse.Success(
            accessToken = accessToken.value,
            refreshToken = refreshToken.value,
            expiresIn = SECONDS.between(accessToken.issuedAt, accessToken.expiresAt),
            scope = accessToken.scopes,
            state = null
        )
    }
}