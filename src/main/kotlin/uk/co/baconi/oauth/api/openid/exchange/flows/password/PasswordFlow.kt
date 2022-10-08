package uk.co.baconi.oauth.api.openid.exchange.flows.password

import uk.co.baconi.oauth.api.customer.CustomerMatchService
import uk.co.baconi.oauth.api.customer.CustomerMatchFailure
import uk.co.baconi.oauth.api.customer.CustomerMatchSuccess
import uk.co.baconi.oauth.api.openid.exchange.ErrorType.InvalidGrant
import uk.co.baconi.oauth.api.openid.exchange.ExchangeResponse
import uk.co.baconi.oauth.api.openid.exchange.FailedExchangeResponse
import uk.co.baconi.oauth.api.openid.exchange.PasswordRequest
import uk.co.baconi.oauth.api.openid.exchange.SuccessExchangeResponse
import uk.co.baconi.oauth.api.openid.exchange.flows.ConfidentialFlow
import uk.co.baconi.oauth.api.tokens.AccessTokenService
import java.time.temporal.ChronoUnit.SECONDS

class PasswordFlow(
    private val matchService: CustomerMatchService,
    private val accessTokenService: AccessTokenService
) : ConfidentialFlow<PasswordRequest> {

    override suspend fun exchange(request: PasswordRequest): ExchangeResponse {

        // TODO - Implement and call the Authentication Service (matching and state)

        // TODO - Implement and call the Authorisation Service (mostly handles scopes)

        return when (val match = matchService.match(username = request.username, password = request.password)) {
            is CustomerMatchFailure -> FailedExchangeResponse(InvalidGrant, "Mismatch")
            is CustomerMatchSuccess -> {

                val accessToken = accessTokenService.issue(
                    username = match.username,
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