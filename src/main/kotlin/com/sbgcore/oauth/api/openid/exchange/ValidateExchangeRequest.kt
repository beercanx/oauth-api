package com.sbgcore.oauth.api.openid.exchange

import arrow.fx.IO
import arrow.fx.extensions.fx
import com.sbgcore.oauth.api.authentication.ConfidentialClient
import com.sbgcore.oauth.api.authentication.ClientPrincipal
import com.sbgcore.oauth.api.authentication.PublicClient
import com.sbgcore.oauth.api.openid.Scopes
import com.sbgcore.oauth.api.openid.exchange.GrantType.*
import com.sbgcore.oauth.api.openid.validateStringParameter
import io.ktor.http.Parameters
import io.ktor.http.URLBuilder
import io.ktor.http.Url

suspend fun validateExchangeRequest(
    principal: ConfidentialClient,
    parameters: Parameters
): IO<ValidatedExchangeRequest<ConfidentialClient>> = IO.fx {

    // Receive the posted form, unless we implement ContentNegotiation that supports URL encoded forms.
    val rawExchangeRequest = !parameters.toRawExchangeRequest()

    when (rawExchangeRequest.grantType) {
        AuthorizationCode -> {
            val code = !rawExchangeRequest.validateStringParameter(RawExchangeRequest::code)
            val redirectUri = !rawExchangeRequest.validateRedirectUri(principal)

            AuthorizationCodeRequest(principal, code, redirectUri)
        }
        Password -> {
            val scopes = !rawExchangeRequest.validateScopes(principal)
            val username = !rawExchangeRequest.validateStringParameter(RawExchangeRequest::username)
            val password = !rawExchangeRequest.validateStringParameter(RawExchangeRequest::password)

            PasswordRequest(principal, scopes, username, password)
        }
        RefreshToken -> {
            val scopes = !rawExchangeRequest.validateScopes(principal)
            val refreshToken = !rawExchangeRequest.validateStringParameter(RawExchangeRequest::refreshToken)

            RefreshTokenRequest(principal, scopes, refreshToken)
        }
        Assertion -> {
            val assertion = !rawExchangeRequest.validateStringParameter(RawExchangeRequest::assertion)

            AssertionRequest(principal, assertion)
        }
        SsoToken -> {
            val ssoToken = !rawExchangeRequest.validateStringParameter(RawExchangeRequest::ssoToken)

            SsoTokenRequest(principal, ssoToken)
        }
    }
}

suspend fun validatePkceExchangeRequest(
    principal: PublicClient,
    parameters: Parameters
): IO<ValidatedExchangeRequest<PublicClient>> = IO.fx {

    // Receive the posted form, unless we implement ContentNegotiation that supports URL encoded forms.
    val raw = !parameters.toRawExchangeRequest()

    if(raw.grantType == AuthorizationCode && raw.isPKCE) {

        val code = !raw.validateStringParameter(RawExchangeRequest::code)
        val redirectUri = !raw.validateRedirectUri(principal)
        val codeVerifier = !raw.validateStringParameter(RawExchangeRequest::codeVerifier)

        PkceAuthorizationCodeRequest(principal, code, redirectUri, codeVerifier)
    } else {
        throw Exception("Bad Request")
    }
}

// TODO - See if we can extend Kotlinx Serialisation to support this instead
private fun Parameters.toRawExchangeRequest(): IO<RawExchangeRequest> = IO.fx {
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

private fun RawExchangeRequest.validateScopes(principal: ConfidentialClient): IO<Set<Scopes>> {
    return validateStringParameter(RawExchangeRequest::scope)
        .map { scopes -> scopes.split(" ") }
        // TODO - Sort out this line, its getting crazy complex
        .flatMap { scopes -> IO.fx { scopes.mapNotNull { string -> Scopes.values().find { scope -> scope.value == string } } } }
        .map { scopes -> scopes.filter { scope -> scope.canBeIssuedTo(principal) } }
        .map { scopes -> scopes.toSet() }
}

private fun Scopes.canBeIssuedTo(principal: ConfidentialClient): Boolean {
    // TODO - Look up from config based on the provided principal id
    return true
}

private fun RawExchangeRequest.validateRedirectUri(principal: ClientPrincipal): IO<Url> = IO.fx {

    val rawRedirectUri = !validateStringParameter(RawExchangeRequest::redirectUri)
    // TODO - Look up from config based on the provided principal id

    URLBuilder(rawRedirectUri).build()
}
