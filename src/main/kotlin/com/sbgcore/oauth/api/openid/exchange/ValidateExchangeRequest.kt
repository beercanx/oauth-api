package com.sbgcore.oauth.api.openid.exchange

import arrow.core.*
import arrow.core.extensions.fx
import com.sbgcore.oauth.api.authentication.ConfidentialClient
import com.sbgcore.oauth.api.authentication.ClientPrincipal
import com.sbgcore.oauth.api.authentication.PublicClient
import com.sbgcore.oauth.api.openid.Scopes
import com.sbgcore.oauth.api.openid.exchange.GrantType.*
import com.sbgcore.oauth.api.openid.validParameter
import io.ktor.http.Parameters
import io.ktor.http.URLBuilder
import io.ktor.http.Url

suspend fun validateExchangeRequest(
    principal: ConfidentialClient,
    parameters: Parameters
): Either<Throwable, ValidatedExchangeRequest<ConfidentialClient>> = Either.fx {

    // Receive the posted form, unless we implement ContentNegotiation that supports URL encoded forms.
    val rawExchangeRequest = !parameters.toRawExchangeRequest()

    when (rawExchangeRequest.grantType) {
        AuthorizationCode -> {
            val code = !validParameter("code", rawExchangeRequest.code)
            val redirectUri = !validRedirectUri(rawExchangeRequest, principal)

            AuthorizationCodeRequest(principal, code, redirectUri)
        }
        Password -> {
            val scopes = !validScopes(rawExchangeRequest, principal)
            val username = !validParameter("username", rawExchangeRequest.username)
            val password = !validParameter("password", rawExchangeRequest.password)

            PasswordRequest(principal, scopes, username, password)
        }
        RefreshToken -> {
            val scopes = !validScopes(rawExchangeRequest, principal)
            val refreshToken = !validParameter("refreshToken", rawExchangeRequest.refreshToken)

            RefreshTokenRequest(principal, scopes, refreshToken)
        }
        Assertion -> {
            val assertion = !validParameter("assertion", rawExchangeRequest.assertion)

            AssertionRequest(principal, assertion)
        }
        SsoToken -> {
            val ssoToken = !validParameter("ssoToken", rawExchangeRequest.ssoToken)

            SsoTokenRequest(principal, ssoToken)
        }
    }
}

suspend fun validatePkceExchangeRequest(
    principal: PublicClient,
    parameters: Parameters
): Either<Throwable, ValidatedExchangeRequest<PublicClient>> = Either.fx {

    // Receive the posted form, unless we implement ContentNegotiation that supports URL encoded forms.
    val raw = !parameters.toRawExchangeRequest()

    if(raw.grantType == AuthorizationCode && raw.isPKCE) {

        val code = !validParameter("code", raw.code)
        val redirectUri = !validRedirectUri(raw, principal)
        val codeVerifier = !validParameter("codeVerifier", raw.codeVerifier)

        PkceAuthorizationCodeRequest(principal, code, redirectUri, codeVerifier)
    } else {
        throw Exception("Bad Request")
    }
}

// TODO - See if we can extend Kotlinx Serialisation to support this instead
private fun Parameters.toRawExchangeRequest(): Either<Throwable, RawExchangeRequest> = Either.fx {
    RawExchangeRequest(
        // All
        grantType = when(get("grant_type")) {
            "authorization_code" -> AuthorizationCode
            "password" -> Password
            "refresh_token" -> RefreshToken
            "urn:ietf:params:oauth:grant-type:jwt-bearer" -> Assertion
            "sso_token" -> SsoToken
            else -> throw Exception("Bad Request")
        },

        // AuthorizationCodeRequest && PkceAuthorizationCodeRequest
        code = get("code"),
        redirectUri = get("redirect_uri"),

        // PkceAuthorizationCodeRequest
        codeVerifier = get("code_verifier"),
        clientId = get("client_id"),

        // PasswordRequest && RefreshTokenRequest
        scope = get("scope"),

        // PasswordRequest
        username = get("username"),
        password = get("password"),

        // RefreshTokenRequest
        refreshToken = get("refresh_token"),

        // AssertionRequest
        assertion = get("assertion"),

        // SsoTokenRequest
        ssoToken = get("sso_token")
    )
}

private fun validScopes(raw: RawExchangeRequest, principal: ConfidentialClient): Either<Throwable, Set<Scopes>> {
    return validParameter("scope", raw.scope)
        .map { scopes -> scopes.split(" ") }
        // TODO - Sort out this line, its getting crazy complex
        .flatMap { scopes -> Either.fx<Throwable, List<Scopes>> { scopes.mapNotNull { string -> Scopes.values().find { scope -> scope.value == string } } } }
        .map { scopes -> scopes.filter { scope -> scope.canBeIssuedTo(principal) } }
        .map { scopes -> scopes.toSet() }
}

private fun Scopes.canBeIssuedTo(principal: ConfidentialClient): Boolean {
    // TODO - Look up from config based on the provided principal id
    return true
}

private fun validRedirectUri(raw: RawExchangeRequest, principal: ClientPrincipal): Either<Throwable, Url> = Either.fx {
    if (!raw.redirectUri.isNullOrBlank()) {
        // TODO - Look up from config based on the provided principal id
        URLBuilder(raw.redirectUri).build()
    } else {
        throw Exception("Null or blank redirect uri")
    }
}
