package com.sbgcore.oauth.api.openid.exchange

import com.sbgcore.oauth.api.authentication.ConfidentialClient
import com.sbgcore.oauth.api.authentication.PublicClient
import com.sbgcore.oauth.api.client.ClientConfigurationRepository
import com.sbgcore.oauth.api.ktor.authenticate
import com.sbgcore.oauth.api.openid.exchange.ErrorType.InvalidRequest
import com.sbgcore.oauth.api.openid.exchange.flows.assertion.AssertionRedemptionFlow
import com.sbgcore.oauth.api.openid.exchange.flows.authorization.AuthorizationCodeFlow
import com.sbgcore.oauth.api.openid.exchange.flows.password.PasswordFlow
import com.sbgcore.oauth.api.openid.exchange.flows.refresh.RefreshFlow
import com.sbgcore.oauth.api.openid.validPublicClient
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.auth.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*

fun Route.tokenExchangeRoute(
    passwordFlow: PasswordFlow,
    refreshFlow: RefreshFlow,
    authorizationCodeFlow: AuthorizationCodeFlow,
    assertionRedemptionFlow: AssertionRedemptionFlow,
    clientConfigurationRepository: ClientConfigurationRepository
) {
    // Optional because we have to cater for public clients using PKCE
    authenticate<ConfidentialClient>(optional = true) {
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
                        is SsoTokenRequest -> TODO("Not yet implemented: $request")
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
                is Parameters -> when (val client = validPublicClient(clientConfigurationRepository, parameters)) {
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
            return@post call.respond(UnauthorizedResponse(HttpAuthHeader.basicAuthChallenge("skybettingandgaming", Charsets.UTF_8)))
        }
    }
}