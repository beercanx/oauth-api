package uk.co.baconi.oauth.api.exchange.grants.password

import uk.co.baconi.oauth.api.authentication.Authentication
import uk.co.baconi.oauth.api.authentication.AuthenticationService
import uk.co.baconi.oauth.api.exchange.ErrorType.InvalidGrant
import uk.co.baconi.oauth.api.exchange.ExchangeResponse
import uk.co.baconi.oauth.api.exchange.FailedExchangeResponse
import uk.co.baconi.oauth.api.exchange.PasswordRequest
import uk.co.baconi.oauth.api.exchange.SuccessExchangeResponse
import uk.co.baconi.oauth.api.exchange.grants.ConfidentialGrant
import uk.co.baconi.oauth.api.tokens.AccessTokenService
import java.time.temporal.ChronoUnit.SECONDS

class PasswordCredentialsGrant(
    private val authenticationService: AuthenticationService,
    private val accessTokenService: AccessTokenService
) : ConfidentialGrant<PasswordRequest> {

    override suspend fun exchange(request: PasswordRequest): ExchangeResponse {

        // TODO - Implement and call the Authorisation Service (mostly handles scopes)

        return when (val authentication = authenticationService.authenticate(request)) {
            is Authentication.Failure -> FailedExchangeResponse(InvalidGrant, authentication.reason.toString()) // TODO - Consider failure descriptions
            is Authentication.Success -> {

                val accessToken = accessTokenService.issue(
                    authentication = authentication,
                    clientId = request.principal.id,
                    scopes = request.scopes
                )

                return SuccessExchangeResponse(
                    accessToken = accessToken.value,
                    expiresIn = SECONDS.between(accessToken.issuedAt, accessToken.expiresAt),
                    scope = accessToken.scopes
                )
            }
        }
    }
}