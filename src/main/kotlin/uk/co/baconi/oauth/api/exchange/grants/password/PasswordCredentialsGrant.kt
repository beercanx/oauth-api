package uk.co.baconi.oauth.api.exchange.grants.password

import uk.co.baconi.oauth.api.authentication.Authentication
import uk.co.baconi.oauth.api.authentication.AuthenticationService
import uk.co.baconi.oauth.api.authorisation.AuthorisationService
import uk.co.baconi.oauth.api.exchange.ErrorType.InvalidGrant
import uk.co.baconi.oauth.api.exchange.ExchangeResponse
import uk.co.baconi.oauth.api.exchange.FailedExchangeResponse
import uk.co.baconi.oauth.api.exchange.PasswordRequest
import uk.co.baconi.oauth.api.exchange.SuccessExchangeResponse
import uk.co.baconi.oauth.api.exchange.grants.ConfidentialGrant
import java.time.temporal.ChronoUnit.SECONDS

class PasswordCredentialsGrant(
    private val authenticationService: AuthenticationService,
    private val authorisationService: AuthorisationService
) : ConfidentialGrant<PasswordRequest> {

    override suspend fun exchange(request: PasswordRequest): ExchangeResponse {

        return when (val authentication = authenticationService.authenticate(request)) {
            // TODO - Validate static error type to reasons
            is Authentication.Failure -> FailedExchangeResponse(InvalidGrant, authentication.reason.toString())
            is Authentication.Success -> {

                val accessToken = authorisationService.authorise(request, authentication)

                return SuccessExchangeResponse(
                    accessToken = accessToken.value,
                    expiresIn = SECONDS.between(accessToken.issuedAt, accessToken.expiresAt),
                    scope = accessToken.scopes
                )
            }
        }
    }
}