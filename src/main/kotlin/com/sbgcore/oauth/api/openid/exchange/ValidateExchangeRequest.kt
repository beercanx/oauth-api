package com.sbgcore.oauth.api.openid.exchange

import com.sbgcore.oauth.api.checkNotBlank
import com.sbgcore.oauth.api.client.ClientPrincipal
import com.sbgcore.oauth.api.client.ConfidentialClient
import com.sbgcore.oauth.api.client.PublicClient
import com.sbgcore.oauth.api.enums.enumByValue
import com.sbgcore.oauth.api.openid.GrantType.*
import com.sbgcore.oauth.api.openid.Scopes
import io.ktor.http.*

fun validateExchangeRequest(
    principal: ConfidentialClient,
    parameters: Parameters
): ConfidentialExchangeRequest = returnOnException(InvalidConfidentialExchangeRequest) {

    // Receive the posted form, unless we implement ContentNegotiation that supports URL encoded forms.
    val raw = parameters.toRawExchangeRequest()

    when (raw.grantType) {
        AuthorizationCode -> {
            val code = checkNotBlank(raw.code) { "code" }
            val redirectUri = raw.validateRedirectUri(principal)

            // TODO - Validate the [code] is a valid code via a repository
            // TODO - Validate the [redirect_uri] is the same as what was used to generate the [code]

            AuthorizationCodeRequest(principal, code, redirectUri)
        }
        Password -> {
            val scopes = raw.validateScopes(principal)
            val username = checkNotBlank(raw.username) { "username" }
            val password = checkNotBlank(raw.password) { "password" }

            PasswordRequest(principal, scopes, username, password)
        }
        RefreshToken -> {
            val scopes = raw.validateScopes(principal)
            val refreshToken = checkNotBlank(raw.refreshToken) { "refreshToken" }

            RefreshTokenRequest(principal, scopes, refreshToken)
        }
        Assertion -> {
            val assertion = checkNotBlank(raw.assertion) { "assertion" }

            AssertionRequest(principal, assertion)
        }
        SsoToken -> {
            val ssoToken = checkNotBlank(raw.ssoToken) { "ssoToken" }

            SsoTokenRequest(principal, ssoToken)
        }
    }
}

fun validatePkceExchangeRequest(
    principal: PublicClient,
    parameters: Parameters
): PublicExchangeRequest = returnOnException(InvalidPublicExchangeRequest) {

    // Receive the posted form, unless we implement ContentNegotiation that supports URL encoded forms.
    val raw = parameters.toRawExchangeRequest()

    if (raw.grantType == AuthorizationCode) {

        val code = checkNotBlank(raw.code) { "code" }
        val redirectUri = raw.validateRedirectUri(principal)
        val codeVerifier = checkNotBlank(raw.codeVerifier) { "codeVerifier" }

        PkceAuthorizationCodeRequest(principal, code, redirectUri, codeVerifier)
    } else {
        InvalidPublicExchangeRequest // TODO - Invalid Grant or Request?
    }
}

// TODO - Extend to support validation failure reasons?
private fun <A : C, B : C, C> returnOnException(onException: B, block: () -> A): C = try {
    block()
} catch (exception: Exception) {
    onException
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
        clientId = get("client_id")?.let(::enumByValue),

        // PasswordRequest && RefreshTokenRequest
        scope = get("scope")?.split(" ")?.mapNotNull { string -> enumByValue<Scopes>(string) }?.toSet(),

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
    return scope?.filter { scope -> scope.canBeIssuedTo(principal) }?.toSet() ?: emptySet()
}

private fun Scopes.canBeIssuedTo(principal: ConfidentialClient): Boolean {
    val configuration = principal.configuration
    return configuration.requiredScopes.contains(this) || configuration.optionalScopes.contains(this)
}

private fun RawExchangeRequest.validateRedirectUri(principal: ClientPrincipal): Url {

    val rawRedirectUri = checkNotBlank(redirectUri) { "redirectUri" }
    val redirectUrl = URLBuilder(rawRedirectUri).build()

    // Design of this system means we expect exact matches for callbacks.
    return if (principal.configuration.redirectUrls.contains(redirectUrl)) {
        redirectUrl
    } else {
        throw Exception("Invalid redirect uri: $redirectUrl")
    }
}