package com.sbgcore.oauth.api.openid.exchange

import com.sbgcore.oauth.api.authentication.ConfidentialClient
import com.sbgcore.oauth.api.authentication.PublicClient
import com.sbgcore.oauth.api.client.ClientId
import com.sbgcore.oauth.api.openid.Scopes
import io.ktor.http.Url
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RawExchangeRequest(
    // All
    @SerialName("grant_type") val grantType: GrantType,

    // AuthorizationCodeRequest && PkceAuthorizationCodeRequest
    val code: String? = null,
    @SerialName("redirect_uri") val redirectUri: String? = null,

    // PkceAuthorizationCodeRequest
    @SerialName("code_verifier") val codeVerifier: String? = null,
    @SerialName("client_id") val clientId: ClientId? = null,

    // PasswordRequest && RefreshTokenRequest
    val scope: Set<Scopes>? = null,

    // PasswordRequest
    val username: String? = null,
    val password: String? = null,

    // RefreshTokenRequest
    @SerialName("refresh_token") val refreshToken: String? = null,

    // AssertionRequest
    val assertion: String? = null,

    // SsoTokenRequest
    @SerialName("sso_token") val ssoToken: String? = null
) {
    override fun toString(): String {
        return "RawExchangeRequest(" +
                "grantType=$grantType, " +
                "code=REDACTED, " +
                "redirectUri=$redirectUri, " +
                "codeVerifier=REDACTED, " +
                "clientId=$clientId, " +
                "scope=$scope, " +
                "username=$username, " +
                "password=REDACTED, " +
                "refreshToken=REDACTED, " +
                "assertion=REDACTED, " +
                "ssoToken=REDACTED" +
                ")"
    }
}

sealed class ConfidentialExchangeRequest

object InvalidConfidentialExchangeRequest : ConfidentialExchangeRequest()

sealed class ValidatedConfidentialExchangeRequest : ConfidentialExchangeRequest() {
    abstract val principal: ConfidentialClient
    override fun toString(): String {
        return "ValidatedConfidentialExchangeRequest(principal=$principal)"
    }
}

sealed class PublicExchangeRequest

object InvalidPublicExchangeRequest : PublicExchangeRequest()

sealed class ValidatedPublicExchangeRequest : PublicExchangeRequest()
{
    abstract val principal: PublicClient
    override fun toString(): String {
        return "ValidatedPublicExchangeRequest(principal=$principal)"
    }
}

data class AuthorizationCodeRequest(
    override val principal: ConfidentialClient,
    val code: String,
    val redirectUri: Url
) : ValidatedConfidentialExchangeRequest() {
    override fun toString(): String {
        return "AuthorizationCodeRequest(principal=$principal, code=REDACTED, redirectUri=$redirectUri)"
    }
}

data class PkceAuthorizationCodeRequest(
    override val principal: PublicClient,
    val code: String,
    val redirectUri: Url,
    val codeVerifier: String
) : ValidatedPublicExchangeRequest() {
    override fun toString(): String {
        return "PkceAuthorizationCodeRequest(principal=$principal, code=REDACTED, redirectUri=$redirectUri, codeVerifier=REDACTED)"
    }
}

data class PasswordRequest(
    override val principal: ConfidentialClient,
    val scopes: Set<Scopes>,
    val username: String,
    val password: String
) : ValidatedConfidentialExchangeRequest() {
    override fun toString(): String {
        return "PasswordRequest(principal=$principal, scopes=$scopes, username='$username', password=REDACTED)"
    }
}

data class RefreshTokenRequest(
    override val principal: ConfidentialClient,
    val scopes: Set<Scopes>,
    val refreshToken: String
) : ValidatedConfidentialExchangeRequest() {
    override fun toString(): String {
        return "RefreshTokenRequest(principal=$principal, scopes=$scopes, refreshToken=REDACTED)"
    }
}

data class AssertionRequest(
    override val principal: ConfidentialClient,
    val assertion: String
) : ValidatedConfidentialExchangeRequest() {
    override fun toString(): String {
        return "AssertionRequest(principal=$principal, assertion=REDACTED)"
    }
}

data class SsoTokenRequest(
    override val principal: ConfidentialClient,
    val ssoToken: String
) : ValidatedConfidentialExchangeRequest() {
    override fun toString(): String {
        return "SsoTokenRequest(principal=$principal, ssoToken=REDACTED)"
    }
}
