package com.sbgcore.oauth.api.openid.exchange

import com.sbgcore.oauth.api.authentication.ConfidentialClient
import com.sbgcore.oauth.api.authentication.PublicClient
import com.sbgcore.oauth.api.openid.Scopes
import io.ktor.auth.Principal
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
    @SerialName("client_id") val clientId: String? = null,

    // PasswordRequest && RefreshTokenRequest
    val scope: String? = null,

    // PasswordRequest
    val username: String? = null,
    val password: String? = null,

    // RefreshTokenRequest
    @SerialName("refresh_token") val refreshToken: String? = null,

    // AssertionRequest
    val assertion: String? = null,

    // SsoTokenRequest
    @SerialName("sso_token") val ssoToken: String? = null
)

sealed class ValidatedExchangeRequest<T : Principal> {
    abstract val principal: T
}

data class AuthorizationCodeRequest(
    override val principal: ConfidentialClient,
    val code: String,
    val redirectUri: Url
) : ValidatedExchangeRequest<ConfidentialClient>()

data class PkceAuthorizationCodeRequest(
    override val principal: PublicClient,
    val code: String,
    val redirectUri: Url,
    val codeVerifier: String
) : ValidatedExchangeRequest<PublicClient>()

data class PasswordRequest(
    override val principal: ConfidentialClient,
    val scopes: Set<Scopes>,
    val username: String,
    val password: String
) : ValidatedExchangeRequest<ConfidentialClient>()

data class RefreshTokenRequest(
    override val principal: ConfidentialClient,
    val scopes: Set<Scopes>,
    val refreshToken: String
) : ValidatedExchangeRequest<ConfidentialClient>()

data class AssertionRequest(
    override val principal: ConfidentialClient,
    val assertion: String
) : ValidatedExchangeRequest<ConfidentialClient>()

data class SsoTokenRequest(
    override val principal: ConfidentialClient,
    val ssoToken: String
) : ValidatedExchangeRequest<ConfidentialClient>()

val RawExchangeRequest.isPKCE: Boolean
    get() = !codeVerifier.isNullOrBlank()