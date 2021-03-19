package com.sbgcore.oauth.api.openid.exchange.flows.password

import com.sbgcore.oauth.api.customer.MatchFailure
import com.sbgcore.oauth.api.customer.MatchService
import com.sbgcore.oauth.api.customer.MatchSuccess
import com.sbgcore.oauth.api.openid.exchange.ErrorType.InvalidGrant
import com.sbgcore.oauth.api.openid.exchange.ExchangeResponse
import com.sbgcore.oauth.api.openid.exchange.FailedExchangeResponse
import com.sbgcore.oauth.api.openid.exchange.PasswordRequest
import com.sbgcore.oauth.api.openid.exchange.flows.ConfidentialFlow

class PasswordFlow(private val matchService: MatchService) : ConfidentialFlow<PasswordRequest> {

    override suspend fun exchange(request: PasswordRequest): ExchangeResponse {

        return when(val match = matchService.match(username = request.username, password = request.password)) {
            is MatchSuccess -> TODO("Issue Tokens")
            is MatchFailure -> FailedExchangeResponse(InvalidGrant, match.failureReason) // TODO - Double check
        }
    }
}