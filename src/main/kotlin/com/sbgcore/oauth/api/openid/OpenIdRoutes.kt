package com.sbgcore.oauth.api.openid

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import com.sbgcore.oauth.api.openid.exchange.*
import com.sbgcore.oauth.api.openid.exchange.flows.AssertionRedemptionFlow
import com.sbgcore.oauth.api.openid.exchange.flows.AuthorizationCodeFlow
import com.sbgcore.oauth.api.openid.exchange.flows.PasswordFlow
import com.sbgcore.oauth.api.openid.exchange.flows.RefreshFlow
import com.sbgcore.oauth.api.openid.introspection.*
import io.ktor.application.Application
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing

fun Application.openIdRoutes(
    passwordFlow: PasswordFlow = PasswordFlow(),
    refreshFlow: RefreshFlow = RefreshFlow(),
    authorizationCodeFlow: AuthorizationCodeFlow = AuthorizationCodeFlow(),
    assertionRedemptionFlow: AssertionRedemptionFlow = AssertionRedemptionFlow(),
    introspectionService: IntrospectionService = IntrospectionService()
) {
    routing {
        route("/openid/v1/token_exchange") {
            post {
                when (val result: Either<Throwable, ValidatedExchangeRequest<*>> = validateExchangeRequest()) {
                    is Right<ValidatedExchangeRequest<*>> -> when (val request = result.b) {
                        is AuthorizationCodeRequest -> authorizationCodeFlow.exchange(request)
                        is PkceAuthorizationCodeRequest -> authorizationCodeFlow.exchange(request)
                        is PasswordRequest -> passwordFlow.exchange(request)
                        is RefreshTokenRequest -> refreshFlow.exchange(request)
                        is AssertionRequest -> assertionRedemptionFlow.exchange(request)
                    }
                    is Left<Throwable> -> {
                        // TODO - Convert Throwable into error responses with context on what to return with
                        // TODO - Handle having a no valid exchange request
                        throw result.a
                    }
                }
            }
        }
        route("/openid/v1/token_introspection") {
            post {
                when (val result: Either<Throwable, ValidatedIntrospectionRequest> = validateIntrospectionRequest()) {
                    is Right<ValidatedIntrospectionRequest> -> when (val request = result.b) {
                        is IntrospectionRequest -> introspectionService.introspect(request)
                        is IntrospectionRequestWithHint -> introspectionService.introspect(request)
                    }
                    is Left<Throwable> -> {
                        // TODO - Convert Throwable into error responses with context on what to return with
                        throw result.a
                    }
                }
            }
        }
    }
}
