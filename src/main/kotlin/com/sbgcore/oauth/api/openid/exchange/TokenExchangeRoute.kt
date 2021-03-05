package com.sbgcore.oauth.api.openid.exchange

import arrow.core.Either
import arrow.core.Some
import com.sbgcore.oauth.api.authentication.ConfidentialClient
import com.sbgcore.oauth.api.authentication.PublicClient
import com.sbgcore.oauth.api.ktor.authenticate
import com.sbgcore.oauth.api.openid.flows.assertion.AssertionRedemptionFlow
import com.sbgcore.oauth.api.openid.flows.authorization.AuthorizationCodeFlow
import com.sbgcore.oauth.api.openid.flows.password.PasswordFlow
import com.sbgcore.oauth.api.openid.flows.refresh.RefreshFlow
import com.sbgcore.oauth.api.openid.validPkceClient
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.http.auth.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.tokenExchangeRoute(
    passwordFlow: PasswordFlow,
    refreshFlow: RefreshFlow,
    authorizationCodeFlow: AuthorizationCodeFlow,
    assertionRedemptionFlow: AssertionRedemptionFlow
) {
    // Optional because we have to cater for PKCE clients
    authenticate<ConfidentialClient>(optional = true) {
        post {
            // Handle standard exchanges
            when (val client = call.principal<ConfidentialClient>()) {
                is ConfidentialClient -> {

                    val parameters = call.receive<Parameters>()

                    // TODO - Make this more IO friendly
                    when (val result = validateExchangeRequest(client, parameters).attempt().suspended()) {
                        is Either.Right<ValidatedExchangeRequest<ConfidentialClient>> -> when (val request = result.b) {
                            is AuthorizationCodeRequest -> authorizationCodeFlow.exchange(request)
                            is PasswordRequest -> passwordFlow.exchange(request)
                            is RefreshTokenRequest -> refreshFlow.exchange(request)
                            is AssertionRequest -> assertionRedemptionFlow.exchange(request)
                        }
                        is Either.Left<Throwable> -> {
                            // TODO - Convert Throwable into error responses with context on what to return with
                            // TODO - Handle having a no valid exchange request
                            throw result.a
                        }
                    }

                    // TODO - Return the appropriate response from the flow exchange performed....

                    // Don't look for other code paths to handle request
                    return@post call.respond(ExchangeResponse("AuthenticatedClient"))
                }
            }

            // Handle PKCE requests
            when (val parameters = call.receiveOrNull<Parameters>()) {
                is Parameters -> when (val client = validPkceClient(parameters)) {
                    is Some<PublicClient> -> {

                        // TODO - Make this more IO friendly
                        when (val result = validatePkceExchangeRequest(client.t, parameters).attempt().suspended()) {
                            is Either.Right<ValidatedExchangeRequest<PublicClient>> -> {
                                authorizationCodeFlow.exchange(result.b as PkceAuthorizationCodeRequest)
                            }
                            is Either.Left<Throwable> -> {
                                // TODO - Convert Throwable into error responses with context on what to return with
                                // TODO - Handle having a no valid exchange request
                                throw result.a
                            }
                        }

                        // Don't look for other code paths to handle request
                        return@post call.respond(ExchangeResponse("PkceClient"))
                    }
                }
            }

            // 401 - Invalid credentials
            // TODO - Setup a common response provider
            return@post call.respond(UnauthorizedResponse(HttpAuthHeader.basicAuthChallenge("skybettingandgaming", Charsets.UTF_8)))
        }
    }
}