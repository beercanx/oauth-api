package com.sbgcore.oauth.api.openid.exchange

import com.sbgcore.oauth.api.authentication.ConfidentialClient
import com.sbgcore.oauth.api.authentication.ClientPrincipal
import com.sbgcore.oauth.api.authentication.PublicClient
import com.sbgcore.oauth.api.openid.Scopes
import com.sbgcore.oauth.api.enumByValue
import com.sbgcore.oauth.api.openid.exchange.GrantType.*
import com.sbgcore.oauth.api.openid.validateStringParameter
import io.ktor.http.Parameters
import io.ktor.http.URLBuilder
import io.ktor.http.Url

fun validateExchangeRequest(
    principal: ConfidentialClient,
    parameters: Parameters
): ValidatedConfidentialExchangeRequest {

    // Receive the posted form, unless we implement ContentNegotiation that supports URL encoded forms.
    val rawExchangeRequest = parameters.toRawExchangeRequest()

    return when (rawExchangeRequest.grantType) {
        AuthorizationCode -> {
            val code = rawExchangeRequest.validateStringParameter(RawExchangeRequest::code)
            val redirectUri = rawExchangeRequest.validateRedirectUri(principal)

            AuthorizationCodeRequest(principal, code, redirectUri)
        }
        Password -> {
            val scopes = rawExchangeRequest.validateScopes(principal)
            val username = rawExchangeRequest.validateStringParameter(RawExchangeRequest::username)
            val password = rawExchangeRequest.validateStringParameter(RawExchangeRequest::password)

            PasswordRequest(principal, scopes, username, password)
        }
        RefreshToken -> {
            val scopes = rawExchangeRequest.validateScopes(principal)
            val refreshToken = rawExchangeRequest.validateStringParameter(RawExchangeRequest::refreshToken)

            RefreshTokenRequest(principal, scopes, refreshToken)
        }
        Assertion -> {
            val assertion = rawExchangeRequest.validateStringParameter(RawExchangeRequest::assertion)

            AssertionRequest(principal, assertion)
        }
        SsoToken -> {
            val ssoToken = rawExchangeRequest.validateStringParameter(RawExchangeRequest::ssoToken)

            SsoTokenRequest(principal, ssoToken)
        }
    }
}

fun validatePkceExchangeRequest(
    principal: PublicClient,
    parameters: Parameters
): ValidatedPublicExchangeRequest {

    // Receive the posted form, unless we implement ContentNegotiation that supports URL encoded forms.
    val raw = parameters.toRawExchangeRequest()

    return if(raw.grantType == AuthorizationCode && raw.isPKCE) {

        val code = raw.validateStringParameter(RawExchangeRequest::code)
        val redirectUri = raw.validateRedirectUri(principal)
        val codeVerifier = raw.validateStringParameter(RawExchangeRequest::codeVerifier)

        PkceAuthorizationCodeRequest(principal, code, redirectUri, codeVerifier)
    } else {
        throw Exception("Bad Request")
    }
}

// TODO - See if we can extend Kotlinx Serialisation to support this instead
private fun Parameters.toRawExchangeRequest(): RawExchangeRequest {

    return RawExchangeRequest(
        // All
        grantType = get("grant_type")?.let(::enumByValue) ?: throw Exception("Bad Request"),

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

private fun RawExchangeRequest.validateScopes(principal: ConfidentialClient): Set<Scopes> {
    return validateStringParameter(RawExchangeRequest::scope)
        .split(" ")
        .mapNotNull { string -> enumByValue<Scopes>(string) }
        .filter { scope -> scope.canBeIssuedTo(principal) }
        .toSet()
}

private fun Scopes.canBeIssuedTo(principal: ConfidentialClient): Boolean {
    // TODO - Look up from config based on the provided principal id
    return true
}

private fun RawExchangeRequest.validateRedirectUri(principal: ClientPrincipal): Url {

    val rawRedirectUri = validateStringParameter(RawExchangeRequest::redirectUri)

    // TODO - Look up from config based on the provided principal id

    return URLBuilder(rawRedirectUri).build()
}