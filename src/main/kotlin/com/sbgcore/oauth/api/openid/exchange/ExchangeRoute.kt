package com.sbgcore.oauth.api.openid.exchange

import com.sbgcore.oauth.api.OAuth2Server.REALM
import com.sbgcore.oauth.api.client.ClientAuthenticationService
import com.sbgcore.oauth.api.client.ConfidentialClient
import com.sbgcore.oauth.api.client.PublicClient
import com.sbgcore.oauth.api.ktor.auth.authenticate
import com.sbgcore.oauth.api.openid.exchange.ErrorType.InvalidRequest
import com.sbgcore.oauth.api.openid.exchange.flows.assertion.AssertionRedemptionFlow
import com.sbgcore.oauth.api.openid.exchange.flows.authorization.AuthorizationCodeFlow
import com.sbgcore.oauth.api.openid.exchange.flows.password.PasswordFlow
import com.sbgcore.oauth.api.openid.exchange.flows.refresh.RefreshFlow
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.auth.*
import io.ktor.http.auth.HttpAuthHeader.Companion.basicAuthChallenge
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import kotlin.text.Charsets.UTF_8

interface ExchangeRoute {

    val passwordFlow: PasswordFlow
    val refreshFlow: RefreshFlow
    val authorizationCodeFlow: AuthorizationCodeFlow
    val assertionRedemptionFlow: AssertionRedemptionFlow
    val clientAuthService: ClientAuthenticationService

    fun Route.exchangeRoute() {

        // Optional because we have to cater for public clients using PKCE
        authenticate(ConfidentialClient::class, optional = true) {
            post {
                // Handle standard exchanges for confidential clients
                when (val client = call.principal<ConfidentialClient>()) {
                    is ConfidentialClient -> {

                        val parameters = call.receive<Parameters>()

                        val response = when (val request = validateExchangeRequest(client, parameters)) {
                            is AuthorizationCodeRequest -> authorizationCodeFlow.exchange(request)
                            is PasswordRequest -> passwordFlow.exchange(request)
                            is RefreshTokenRequest -> refreshFlow.exchange(request)
                            is AssertionRequest -> assertionRedemptionFlow.exchange(request)
                            is InvalidConfidentialExchangeRequest -> FailedExchangeResponse(InvalidRequest) // TODO - Extend to include more detail?
                        }

                        return@post when(response) {
                            is SuccessExchangeResponse -> call.respond(response)
                            is FailedExchangeResponse ->  call.respond(BadRequest, response) // TODO - Review if the spec allows for any other type of response codes, maybe around invalid_client responses.
                        }
                    }
                }

                // Handle PKCE requests for public clients
                when (val parameters = call.receiveOrNull<Parameters>()) {
                    is Parameters -> when (val client = parameters["client_id"]?.let(clientAuthService::publicClient)) {
                        is PublicClient -> {

                            val response = when (val result = validatePkceExchangeRequest(client, parameters)) {
                                is PkceAuthorizationCodeRequest -> authorizationCodeFlow.exchange(result)
                                is InvalidPublicExchangeRequest -> FailedExchangeResponse(InvalidRequest) // TODO - Extend to include more detail?
                            }

                            return@post when(response) {
                                is SuccessExchangeResponse -> call.respond(response)
                                is FailedExchangeResponse ->  call.respond(BadRequest, response) // TODO - Review if the spec allows for any other type of response codes, maybe around invalid_client responses.
                            }
                        }
                    }
                }

                // 401 - Invalid credentials?
                // TODO - Review this "default" error response
                // TODO - Setup a common response provider
                return@post call.respond(UnauthorizedResponse(basicAuthChallenge(REALM, UTF_8)))
            }
        }
    }
}
