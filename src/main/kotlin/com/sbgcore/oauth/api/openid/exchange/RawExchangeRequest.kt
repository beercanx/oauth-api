package com.sbgcore.oauth.api.openid.exchange

import com.sbgcore.oauth.api.authentication.AuthenticatedClient
import com.sbgcore.oauth.api.authentication.PkceClient
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
    override val principal: AuthenticatedClient,
    val code: String,
    val redirectUri: Url
) : ValidatedExchangeRequest<AuthenticatedClient>()

data class PkceAuthorizationCodeRequest(
    override val principal: PkceClient,
    val code: String,
    val redirectUri: Url,
    val codeVerifier: String
) : ValidatedExchangeRequest<PkceClient>()

data class PasswordRequest(
    override val principal: AuthenticatedClient,
    val scopes: Set<Scopes>,
    val username: String,
    val password: String
) : ValidatedExchangeRequest<AuthenticatedClient>()

data class RefreshTokenRequest(
    override val principal: AuthenticatedClient,
    val scopes: Set<Scopes>,
    val refreshToken: String
) : ValidatedExchangeRequest<AuthenticatedClient>()

data class AssertionRequest(
    override val principal: AuthenticatedClient,
    val assertion: String
) : ValidatedExchangeRequest<AuthenticatedClient>()

data class SsoTokenRequest(
    override val principal: AuthenticatedClient,
    val ssoToken: String
) : ValidatedExchangeRequest<AuthenticatedClient>()

val RawExchangeRequest.isPKCE: Boolean
    get() = !codeVerifier.isNullOrBlank()