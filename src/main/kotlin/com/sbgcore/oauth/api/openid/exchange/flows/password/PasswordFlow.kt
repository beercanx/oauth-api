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

        return when(val match = matchService.match(username = request.username, password = request.password)) {
            is MatchFailure -> FailedExchangeResponse(InvalidGrant, match.reason) // TODO - Double check
            is MatchSuccess -> {

                val accessToken = accessTokenService.issue(
                    customerId = match.customerId,
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