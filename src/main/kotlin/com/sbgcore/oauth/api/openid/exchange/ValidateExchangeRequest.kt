package com.sbgcore.oauth.api.openid.exchange

import arrow.core.Either
import arrow.core.extensions.fx
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.sbgcore.oauth.api.authentication.AuthenticatedClient
import com.sbgcore.oauth.api.authentication.ClientPrincipal
import com.sbgcore.oauth.api.authentication.PkceClient
import com.sbgcore.oauth.api.openid.Scopes
import com.sbgcore.oauth.api.openid.validClientPrincipal
import com.sbgcore.oauth.api.openid.validParameter
import io.ktor.application.ApplicationCall
import io.ktor.application.application
import io.ktor.application.call
import io.ktor.application.log
import io.ktor.auth.principal
import io.ktor.http.Parameters
import io.ktor.http.URLBuilder
import io.ktor.http.Url
import io.ktor.request.receive
import io.ktor.util.pipeline.PipelineContext

suspend fun PipelineContext<*, ApplicationCall>.validateExchangeRequest(): Either<Throwable, ValidatedExchangeRequest<*>> {

    // TODO - Handle deserialization errors
    // val rawExchangeRequest = call.receive<RawExchangeRequest>()

    // Receive the posted form, unless we implement ContentNegotiation that supports URL encoded forms.
    val p = call.parameters
    val raw = call.receive<Parameters>()

    application.log.debug("call.parameters: {}", p)
    application.log.debug("call.receive<Parameters>(): {}", raw)

    val rawExchangeRequest = RawExchangeRequest(GrantType.AuthorizationCode)

    return when (rawExchangeRequest.grantType) {
        GrantType.AuthorizationCode -> {
            if (rawExchangeRequest.isPKCE) {
                Either.fx {
                    val principal = !validPkceClient(rawExchangeRequest)
                    val code = !validParameter("code", rawExchangeRequest.code)
                    val redirectUri = !validRedirectUri(rawExchangeRequest, principal)
                    val codeVerifier = !validParameter("codeVerifier", rawExchangeRequest.codeVerifier)

                    PkceAuthorizationCodeRequest(principal, code, redirectUri, codeVerifier)
                }
            } else {
                Either.fx {
                    val principal = !validClientPrincipal(call.principal<AuthenticatedClient>())
                    val code = !validParameter("code", rawExchangeRequest.code)
                    val redirectUri = !validRedirectUri(rawExchangeRequest, principal)

                    AuthorizationCodeRequest(principal, code, redirectUri)
                }
            }
        }
        GrantType.Password -> Either.fx {
            val principal = !validClientPrincipal(call.principal<AuthenticatedClient>())
            val scopes = !validScopes(rawExchangeRequest, principal)
            val username = !validParameter("username", rawExchangeRequest.username)
            val password = !validParameter("password", rawExchangeRequest.password)

            PasswordRequest(principal, scopes, username, password)
        }
        GrantType.RefreshToken -> Either.fx {
            val principal = !validClientPrincipal(call.principal<AuthenticatedClient>())
            val scopes = !validScopes(rawExchangeRequest, principal)
            val refreshToken = !validParameter("refreshToken", rawExchangeRequest.refreshToken)

            RefreshTokenRequest(principal, scopes, refreshToken)
        }
        GrantType.Assertion -> Either.fx {
            val principal = !validClientPrincipal(call.principal<AuthenticatedClient>())
            val assertion = !validParameter("assertion", rawExchangeRequest.assertion)

            AssertionRequest(principal, assertion)
        }
        GrantType.SsoToken -> Either.fx {
            val principal = !validClientPrincipal(call.principal<AuthenticatedClient>())
            val ssoToken = !validParameter("ssoToken", rawExchangeRequest.ssoToken)

            SsoTokenRequest(principal, ssoToken)
        }
    }
}

fun validScopes(request: RawExchangeRequest, principal: AuthenticatedClient): Either<Throwable, Set<Scopes>> {
    return validParameter("scope", request.scope)
        .map { scopes -> scopes.split(" ") }
        .flatMap { scopes -> Either.fx<Throwable, List<Scopes>> { scopes.map(Scopes::valueOf) } }
        .map { scopes -> scopes.filter { scope -> scope.canBeIssuedTo(principal) } }
        .map { scopes -> scopes.toSet() }
}

fun Scopes.canBeIssuedTo(principal: AuthenticatedClient): Boolean {
    // TODO - Look up from config based on the provided principal id
    return true
}

fun validRedirectUri(request: RawExchangeRequest, principal: ClientPrincipal): Either<Throwable, Url> = Either.fx {
    if (!request.redirectUri.isNullOrBlank()) {
        // TODO - Look up from config based on the provided principal id
        URLBuilder(request.redirectUri).build()
    } else {
        throw Exception("Null or blank redirect uri")
    }
}

fun validPkceClient(request: RawExchangeRequest): Either<Throwable, PkceClient> {
    return if (!request.clientId.isNullOrBlank()) {
        // TODO - Lookup from config
        PkceClient(request.clientId).right()
    } else {
        Exception("Invalid PKCE client").left()
    }
}