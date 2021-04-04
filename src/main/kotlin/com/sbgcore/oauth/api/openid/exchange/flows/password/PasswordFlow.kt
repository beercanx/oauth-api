package com.sbgcore.oauth.api.openid.exchange.flows.password

import com.sbgcore.oauth.api.customer.MatchFailure
import com.sbgcore.oauth.api.customer.MatchService
import com.sbgcore.oauth.api.customer.MatchSuccess
import com.sbgcore.oauth.api.openid.exchange.ErrorType.InvalidGrant
import com.sbgcore.oauth.api.openid.exchange.ExchangeResponse
import com.sbgcore.oauth.api.openid.exchange.FailedExchangeResponse
import com.sbgcore.oauth.api.openid.exchange.PasswordRequest
import com.sbgcore.oauth.api.openid.exchange.SuccessExchangeResponse
import com.sbgcore.oauth.api.openid.exchange.flows.ConfidentialFlow
import com.sbgcore.oauth.api.tokens.AccessTokenService
import java.time.temporal.ChronoUnit.SECONDS

class PasswordFlow(
    private val matchService: MatchService,
    private val accessTokenService: AccessTokenService
) : ConfidentialFlow<PasswordRequest> {

    override suspend fun exchange(request: PasswordRequest): ExchangeResponse {

        // TODO - Implement and call the Authentication Service (matching and state)

        // TODO - Implement and call the Authorisation Service (mostly handles scopes)

        return when(val match = matchService.match(username = request.username, password = request.password)) {
            is MatchFailure -> FailedExchangeResponse(InvalidGrant, "Mismatch")
            is MatchSuccess -> {

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