package com.sbgcore.oauth.api.openid.exchange.flows.password

import com.sbgcore.oauth.api.customer.CustomerMatchService
import com.sbgcore.oauth.api.customer.CustomerMatchFailure
import com.sbgcore.oauth.api.customer.CustomerMatchSuccess
import com.sbgcore.oauth.api.openid.exchange.ErrorType.InvalidGrant
import com.sbgcore.oauth.api.openid.exchange.ExchangeResponse
import com.sbgcore.oauth.api.openid.exchange.FailedExchangeResponse
import com.sbgcore.oauth.api.openid.exchange.PasswordRequest
import com.sbgcore.oauth.api.openid.exchange.SuccessExchangeResponse
import com.sbgcore.oauth.api.openid.exchange.flows.ConfidentialFlow
import com.sbgcore.oauth.api.tokens.AccessTokenService
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