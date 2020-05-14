package com.sbgcore.oauth.api.openid.exchange

import arrow.core.*
import arrow.core.extensions.fx
import com.sbgcore.oauth.api.authentication.AuthenticatedClientPrincipal
import com.sbgcore.oauth.api.authentication.ClientPrincipal
import com.sbgcore.oauth.api.authentication.PkceClientPrincipal
import com.sbgcore.oauth.api.openid.Scopes
import com.sbgcore.oauth.api.openid.validClientPrincipal
import com.sbgcore.oauth.api.openid.validParameter
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.principal
import io.ktor.http.URLBuilder
import io.ktor.http.Url
import io.ktor.request.receive
import io.ktor.util.pipeline.PipelineContext

suspend fun PipelineContext<*, ApplicationCall>.validateExchangeRequest(): Either<Throwable, ValidatedExchangeRequest<*>> {

    // TODO - Handle deserialisation errors
    val rawRequest = call.receive<ExchangeRequest>()

    return when(rawRequest.grantType) {
        GrantType.AuthorizationCode -> {
            if(rawRequest.isPKCE) {
                Either.fx {
                    val (principal) = validPkceClient(rawRequest)
                    val (code) = validParameter("code", rawRequest.code)
                    val (redirectUri) = validRedirectUri(rawRequest, principal)
                    val (codeVerifier) = validParameter("codeVerifier", rawRequest.codeVerifier)

                    PkceAuthorizationCodeRequest(principal, code, redirectUri, codeVerifier)
                }
            } else {
                Either.fx {
                    val (principal) = validClientPrincipal(call.principal<AuthenticatedClientPrincipal>())
                    val (code) = validParameter("code", rawRequest.code)
                    val (redirectUri) = validRedirectUri(rawRequest, principal)

                    AuthorizationCodeRequest(principal, code, redirectUri)
                }
            }
        }
        GrantType.Password -> Either.fx {
            val (principal) = validClientPrincipal(call.principal<AuthenticatedClientPrincipal>())
            val (scopes) = validScopes(rawRequest, principal)
            val (username) = validParameter("username", rawRequest.username)
            val (password) = validParameter("password", rawRequest.password)

            PasswordRequest(principal, scopes, username, password)
        }
        GrantType.RefreshToken -> Either.fx {
            val (principal) = validClientPrincipal(call.principal<AuthenticatedClientPrincipal>())
            val (scopes) = validScopes(rawRequest, principal)
            val (refreshToken) = validParameter("refreshToken", rawRequest.refreshToken)

            RefreshTokenRequest(principal, scopes, refreshToken)
        }
        GrantType.Assertion -> Either.fx {
            val (principal) = validClientPrincipal(call.principal<AuthenticatedClientPrincipal>())
            val (assertion) = validParameter("assertion", rawRequest.assertion)

            AssertionRequest(principal, assertion)
        }
        GrantType.SsoToken -> Either.fx {
            val (principal) = validClientPrincipal(call.principal<AuthenticatedClientPrincipal>())
            val (ssoToken) = validParameter("ssoToken", rawRequest.ssoToken)

            SsoTokenRequest(principal, ssoToken)
        }
    }
}

fun validScopes(request: ExchangeRequest, principal: AuthenticatedClientPrincipal): Either<Throwable, Set<Scopes>> {
    return validParameter("scope", request.scope)
        .map { scopes -> scopes.split(" ") }
        .flatMap { scopes -> Either.fx { scopes.map(Scopes::valueOf) } }
        .map { scopes -> scopes.filter { scope -> scope.canBeIssuedTo(principal) } }
        .map { scopes -> scopes.toSet()}
}

fun Scopes.canBeIssuedTo(principal: AuthenticatedClientPrincipal): Boolean {
    // TODO - Look up from config based on the provided principal id
    return true;
}

fun validRedirectUri(request: ExchangeRequest, principal: ClientPrincipal): Either<Throwable, Url> = Either.fx {
    if(!request.redirectUri.isNullOrBlank()) {
        // TODO - Look up from config based on the provided principal id
        URLBuilder(request.redirectUri).build()
    } else {
        throw Exception("Null or blank redirect uri")
    }
}

fun validPkceClient(request: ExchangeRequest): Either<Throwable, PkceClientPrincipal> {
    return if(!request.clientId.isNullOrBlank()) {
        // TODO - Lookup from config
        PkceClientPrincipal(request.clientId).right()
    } else {
        Exception("Invalid PKCE client").left()
    }
}