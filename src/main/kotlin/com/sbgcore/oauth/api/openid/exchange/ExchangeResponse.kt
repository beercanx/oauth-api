@file:UseSerializers(UUIDSerializer::class)

package com.sbgcore.oauth.api.openid.exchange

import com.sbgcore.oauth.api.customer.FailureReason
import com.sbgcore.oauth.api.openid.Scopes
import com.sbgcore.oauth.api.openid.SerializableEnum
import com.sbgcore.oauth.api.openid.exchange.tokens.TokenType
import com.sbgcore.oauth.api.openid.exchange.tokens.TokenType.Bearer
import com.sbgcore.oauth.api.serializers.ScopeSerializer
import com.sbgcore.oauth.api.serializers.UUIDSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.util.*

sealed class ExchangeResponse

/**
 * https://tools.ietf.org/html/rfc6749#section-5.1
 */
@Serializable
data class SuccessExchangeResponse(
    /**
     * The access token issued by the authorization server.
     */
    @SerialName("access_token") val accessToken: UUID,

    /**
     * The type of the token issued as described in https://tools.ietf.org/html/rfc6749#section-7.1
     */
    @SerialName("token_type") val tokenType: TokenType = Bearer,

    /**
     * The lifetime in seconds of the access token.  For example, the value "3600" denotes that the access token will
     * expire in one hour from the time the response was generated. If omitted, the authorization server SHOULD provide
     * the expiration time via other means or document the default value.
     */
    @SerialName("expires_in") val expiresIn: Long,

    /**
     * OPTIONAL if identical to the scope requested by the client; otherwise, REQUIRED.
     * The scope of the access token as described by https://tools.ietf.org/html/rfc6749#section-3.3
     */
    @Serializable(with = ScopeSerializer::class) val scope: Set<Scopes>

) : ExchangeResponse()

/**
 * https://tools.ietf.org/html/rfc6749#section-5.2
 */
@Serializable
data class FailedExchangeResponse(

    /**
     * A single ASCII error code from the defined list.
     */
    val error: ErrorType,

    /**
     * Human-readable ASCII text providing additional information, used to assist the client developer in
     * understanding the error that occurred.
     */
    val error_description: String? = null

) : ExchangeResponse() {
    constructor(error: ErrorType, failure: FailureReason) : this(error, failure.toString())
}

@Serializable
enum class ErrorType : SerializableEnum {

    /**
     * The request is missing a required parameter, includes an unsupported parameter value (other than grant type),
     * repeats a parameter, includes multiple credentials, utilizes more than one mechanism for authenticating the
     * client, or is otherwise malformed.
     */
    @SerialName("invalid_request") InvalidRequest,

    /**
     * Client authentication failed (e.g., unknown client, no client authentication included, or unsupported
     * authentication method).  The authorization server MAY return an HTTP 401 (Unauthorized) status code to indicate
     * which HTTP authentication schemes are supported.  If the client attempted to authenticate via the "Authorization"
     * request header field, the authorization server MUST respond with an HTTP 401 (Unauthorized) status code and
     * include the "WWW-Authenticate" response header field matching the authentication scheme used by the client.
     */
    @SerialName("invalid_client") InvalidClient,

    /**
     * The provided authorization grant (e.g., authorization code, resource owner credentials) or refresh token is
     * invalid, expired, revoked, does not match the redirection URI used in the authorization request, or was issued to
     * another client.
     */
    @SerialName("invalid_grant") InvalidGrant,

    /**
     * The authenticated client is not authorized to use this authorization grant type.
     */
    @SerialName("unauthorized_client") UnauthorizedClient,

    /**
     * The authorization grant type is not supported by the authorization server.
     */
    @SerialName("unsupported_grant_type") UnsupportedGrantType,

    /**
     * The requested scope is invalid, unknown, malformed, or exceeds the scope granted by the resource owner.
     */
    @SerialName("invalid_scope") InvalidScope,

    ;

    override val value: String by lazy {
        getSerialName(serializer())
    }
}