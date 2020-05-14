package com.sbgcore.oauth.api.openid.exchange

import com.sbgcore.oauth.api.authentication.AuthenticatedClientPrincipal
import com.sbgcore.oauth.api.openid.Scopes
import com.sbgcore.oauth.api.authentication.PkceClientPrincipal
import io.ktor.auth.Principal
import io.ktor.http.Url
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExchangeRequest(
    // All
    @SerialName("grant_type") val grantType: GrantType,

    // AuthorizationCodeRequest && PkceAuthorizationCodeRequest
    val code: String?,
    @SerialName("redirect_uri") val redirectUri: String?,

    // PkceAuthorizationCodeRequest
    @SerialName("code_verifier") val codeVerifier: String?,
    @SerialName("client_id") val clientId: String?,

    // PasswordRequest && RefreshTokenRequest
    val scope: String?,

    // PasswordRequest
    val username: String?,
    val password: String?,

    // RefreshTokenRequest
    @SerialName("refresh_token") val refreshToken: String?,

    // AssertionRequest
    val assertion: String?,

    // SsoTokenRequest
    @SerialName("sso_token") val ssoToken: String?
)

sealed class ValidatedExchangeRequest<T : Principal> {
    abstract val principal: T
}

data class AuthorizationCodeRequest(
    override val principal: AuthenticatedClientPrincipal,
    val code: String,
    val redirectUri: Url
) : ValidatedExchangeRequest<AuthenticatedClientPrincipal>()

data class PkceAuthorizationCodeRequest(
    override val principal: PkceClientPrincipal,
    val code: String,
    val redirectUri: Url,
    val codeVerifier: String
) : ValidatedExchangeRequest<PkceClientPrincipal>()

data class PasswordRequest(
    override val principal: AuthenticatedClientPrincipal,
    val scopes: Set<Scopes>,
    val username: String,
    val password: String
) : ValidatedExchangeRequest<AuthenticatedClientPrincipal>()

data class RefreshTokenRequest(
    override val principal: AuthenticatedClientPrincipal,
    val scopes: Set<Scopes>,
    val refreshToken: String
) : ValidatedExchangeRequest<AuthenticatedClientPrincipal>()

data class AssertionRequest(
    override val principal: AuthenticatedClientPrincipal,
    val assertion: String
) : ValidatedExchangeRequest<AuthenticatedClientPrincipal>()

data class SsoTokenRequest(
    override val principal: AuthenticatedClientPrincipal,
    val ssoToken: String
) : ValidatedExchangeRequest<AuthenticatedClientPrincipal>()

val ExchangeRequest.isPKCE: Boolean
    get() = !codeVerifier.isNullOrBlank()