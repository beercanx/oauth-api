@file:UseSerializers(UUIDSerializer::class)

package uk.co.baconi.oauth.api.token

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import uk.co.baconi.oauth.api.common.scope.Scope
import uk.co.baconi.oauth.api.common.scope.ScopesSerializer
import uk.co.baconi.oauth.api.common.token.TokenType
import uk.co.baconi.oauth.api.common.token.TokenType.Bearer
import uk.co.baconi.oauth.api.common.uuid.UUIDSerializer
import java.util.*

sealed interface TokenResponse {

    /**
     * https://www.rfc-editor.org/rfc/rfc6749#section-5.1
     */
    @Serializable
    data class Success(
        /**
         * The access token issued by the authorization server.
         */
        @SerialName("access_token") val accessToken: UUID,

        /**
         * The type of the token issued as described in https://www.rfc-editor.org/rfc/rfc6749#section-7.1
         */
        @SerialName("token_type") val tokenType: TokenType = Bearer,

        /**
         * The lifetime in seconds of the access token.  For example, the value "3600" denotes that the access token will
         * expire in one hour from the time the response was generated. If omitted, the authorization server SHOULD provide
         * the expiration time via other means or document the default value.
         */
        @SerialName("expires_in") val expiresIn: Long,

        /**
         * OPTIONAL. The refresh token, which can be used to obtain new access tokens using the same authorization
         * grant as described in https://www.rfc-editor.org/rfc/rfc6749#section-6
         */
        @SerialName("refresh_token") val refreshToken: UUID,

        /**
         * OPTIONAL if identical to the scope requested by the client; otherwise, REQUIRED.
         * The scope of the access token as described by https://www.rfc-editor.org/rfc/rfc6749#section-3.3
         */
        @Serializable(with = ScopesSerializer::class) val scope: Set<Scope>,

        /**
         * REQUIRED if the "state" parameter was present in the client authorization request.
         * The exact value received from the client.
         */
        val state: String?,

        ) : TokenResponse {
        override fun toString(): String {
            return "Success(accessToken='REDACTED', tokenType=$tokenType, expiresIn=$expiresIn, refreshToken='REDACTED', scope=$scope, state='REDACTED')"
        }
    }

    /**
     * https://www.rfc-editor.org/rfc/rfc6749#section-5.2
     */
    @Serializable
    data class Failed(
        /**
         * A single ASCII error code from the defined list.
         */
        val error: TokenErrorType,

        /**
         * Human-readable ASCII text providing additional information, used to assist the client developer in
         * understanding the error that occurred.
         */
        @SerialName("error_description")
        val errorDescription: String

    ) : TokenResponse {
        init {
            require(errorDescription.isNotBlank()) { "Error description should not be blank!" }
        }
    }
}