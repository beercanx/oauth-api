package com.sbgcore.oauth.api.openid

import arrow.core.*
import arrow.core.Either.Left
import arrow.core.Either.Right
import com.sbgcore.oauth.api.authentication.AuthenticatedClient
import com.sbgcore.oauth.api.authentication.PkceClient
import com.sbgcore.oauth.api.ktor.authenticate
import com.sbgcore.oauth.api.openid.exchange.*
import com.sbgcore.oauth.api.openid.exchange.flows.AssertionRedemptionFlow
import com.sbgcore.oauth.api.openid.exchange.flows.AuthorizationCodeFlow
import com.sbgcore.oauth.api.openid.exchange.flows.PasswordFlow
import com.sbgcore.oauth.api.openid.exchange.flows.RefreshFlow
import com.sbgcore.oauth.api.openid.introspection.*
import io.ktor.application.*
import io.ktor.auth.UnauthorizedResponse
import io.ktor.auth.authentication
import io.ktor.auth.principal
import io.ktor.http.Parameters
import io.ktor.http.auth.HttpAuthHeader.Companion.basicAuthChallenge
import io.ktor.request.receive
import io.ktor.request.receiveOrNull
import io.ktor.response.respond
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.util.pipeline.PipelineContext
import io.ktor.utils.io.charsets.Charsets
import kotlinx.serialization.Serializable

@Serializable
data class ExchangeResponse(val type: String) // TODO - Replace with some thing real

fun Application.openIdRoutes(
    passwordFlow: PasswordFlow = PasswordFlow(),
    refreshFlow: RefreshFlow = RefreshFlow(),
    authorizationCodeFlow: AuthorizationCodeFlow = AuthorizationCodeFlow(),
    assertionRedemptionFlow: AssertionRedemptionFlow = AssertionRedemptionFlow(),
    introspectionService: IntrospectionService = IntrospectionService()
) {
    routing {
        route("/openid/v1/token_exchange") {
            // Optional because we have to cater for PKCE clients
            authenticate<AuthenticatedClient>(optional = true) {
                post {

                    // Handle standard exchanges
                    when (val client = call.principal<AuthenticatedClient>()) {
                        is AuthenticatedClient -> {

                            val parameters = call.receive<Parameters>()

                            when (val result = validateExchangeRequest(client, parameters)) {
                                is Right<ValidatedExchangeRequest<AuthenticatedClient>> -> when (val request = result.b) {
                                    is AuthorizationCodeRequest -> authorizationCodeFlow.exchange(request)
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

                            call.respond(ExchangeResponse("AuthenticatedClient"))

                            // Don't look for other code paths to handle request
                            return@post
                        }
                    }

                    // Handle PKCE requests
                    when (val parameters = call.receiveOrNull<Parameters>()) {
                        is Parameters -> when (val client = validPkceClient(parameters)) {
                            is Some<PkceClient> -> {

                                when (val result = validatePkceExchangeRequest(client.t, parameters)) {
                                    is Right<ValidatedExchangeRequest<PkceClient>> -> {
                                        authorizationCodeFlow.exchange(result.b as PkceAuthorizationCodeRequest)
                                    }
                                    is Left<Throwable> -> {
                                        // TODO - Convert Throwable into error responses with context on what to return with
                                        // TODO - Handle having a no valid exchange request
                                        throw result.a
                                    }
                                }

                                call.respond(ExchangeResponse("PkceClient"))

                                // Don't look for other code paths to handle request
                                return@post
                            }
                        }
                    }
                    // 401 - Invalid credentials
                    call.respond(UnauthorizedResponse(basicAuthChallenge("skybettingandgaming", Charsets.UTF_8)))
                }
            }
        }
        route("/openid/v1/token_introspection") {
            authenticate<AuthenticatedClient> {
                post {
                    when (val result = validateIntrospectionRequest()) {
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
}

/**
 * Extract the client_id from the body, check that its a valid PKCE client and then save it as the current Principal.
 */
private fun PipelineContext<*, ApplicationCall>.validPkceClient(parameters: Parameters): Option<PkceClient> {
    return parameters["client_id"].toOption().flatMap(::validatePkceClient).also { client ->
        if (client is Some<PkceClient>) call.authentication.principal(client.t)
    }
}

/**
 * TODO - Lookup against client config / database
 */
private fun validatePkceClient(clientId: String): Option<PkceClient> {
    return if(clientId.isBlank()) {
        none()
    } else {
        PkceClient(clientId).some()
    }
}
