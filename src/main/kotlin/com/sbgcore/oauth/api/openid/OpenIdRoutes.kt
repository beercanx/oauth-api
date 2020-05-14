package com.sbgcore.oauth.api.openid

import arrow.core.Either
import com.sbgcore.oauth.api.openid.exchange.*
import com.sbgcore.oauth.api.openid.exchange.flows.AuthorizationCodeFlow
import com.sbgcore.oauth.api.openid.exchange.flows.PasswordFlow
import com.sbgcore.oauth.api.openid.exchange.flows.RefreshFlow
import com.sbgcore.oauth.api.openid.introspection.IntrospectionRequest
import com.sbgcore.oauth.api.openid.introspection.IntrospectionRequestWithHint
import com.sbgcore.oauth.api.openid.introspection.ValidatedIntrospectionRequest
import com.sbgcore.oauth.api.openid.introspection.validateIntrospectionRequest
import io.ktor.application.Application
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing

fun Application.openIdRoutes(
    passwordFlow: PasswordFlow = PasswordFlow(),
    refreshFlow: RefreshFlow = RefreshFlow(),
    authorizationCodeFlow: AuthorizationCodeFlow = AuthorizationCodeFlow()
) {
    routing {
        // Token Exchange Endpoint
        route("/openid/v1/token_exchange") {
            post {
                when(val result = validateExchangeRequest()) {
                    is Either.Right<ValidatedExchangeRequest<*>> -> when(val request = result.b) {
                        is AuthorizationCodeRequest -> authorizationCodeFlow.exchange(request)
                        is PkceAuthorizationCodeRequest -> authorizationCodeFlow.exchange(request)
                        is PasswordRequest -> passwordFlow.exchange(request)
                        is RefreshTokenRequest -> refreshFlow.exchange(request)
                    }
                    is Either.Left<Throwable> -> {
                        // TODO - Convert Throwable into error responses with context on what to return with
                        // TODO - Handle having a no valid exchange request
                        throw result.a;
                    }
                }
            }
        }
        route("/openid/v1/token_introspection") {
            post {
                when(val result = validateIntrospectionRequest()) {
                    is Either.Right<ValidatedIntrospectionRequest> -> when(val request = result.b) {
                        is IntrospectionRequest -> TODO("Implement token introspection")
                        is IntrospectionRequestWithHint -> TODO("Implement token introspection with hint")
                    }
                    is Either.Left<Throwable> -> {
                        // TODO - Convert Throwable into error responses with context on what to return with
                        throw result.a;
                    }
                }
            }
        }
    }
}
