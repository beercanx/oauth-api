package uk.co.baconi.oauth.api.introspection

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import uk.co.baconi.oauth.api.client.ClientId
import uk.co.baconi.oauth.api.scopes.Scopes
import uk.co.baconi.oauth.api.serializers.ScopeSerializer
import uk.co.baconi.oauth.api.tokens.TokenType
import uk.co.baconi.oauth.api.tokens.Tokens

sealed class IntrospectionResponse

/**
 * https://datatracker.ietf.org/doc/html/rfc7662#section-2.2
 */
@Serializable
data class ActiveIntrospectionResponse(

    /**
     * Boolean indicator of whether or not the presented token is currently active.
     *
     * The specifics of a token's "active" state will vary depending on the implementation of the authorization server
     * and the information it keeps about its tokens, but a "true" value return for the "active" property will generally
     * indicate that a given token has been issued by this authorization server, has not been revoked by the resource
     * owner, and is within its given time window of validity (e.g., after its issuance time and before its expiration
     * time).
     *
     * See Section 4 for information on implementation of such checks [https://datatracker.ietf.org/doc/html/rfc7662#section-4].
     */
    val active: Boolean = true,

    /**
     * A JSON string containing a space-separated list of scopes associated with this token, in the format described
     * in Section 3.3 of OAuth 2.0 [https://datatracker.ietf.org/doc/html/rfc6749].
     */
    @Serializable(with = ScopeSerializer::class) val scope: Set<Scopes>,

    /**
     * Client identifier for the OAuth 2.0 client that requested this token.
     */
    @SerialName("client_id") val clientId: ClientId,

    /**
     * Human-readable identifier for the resource owner who authorized this token.
     */
    val username: String,

    /**
     * Type of the token as defined in Section 5.1 of OAuth 2.0 [https://datatracker.ietf.org/doc/html/rfc6749].
     */
    @SerialName("token_type") val tokenType: TokenType,

    /**
     * Integer timestamp, measured in the number of seconds since January 1 1970 UTC, indicating when this token will
     * expire, as defined in JWT [https://datatracker.ietf.org/doc/html/rfc7519].
     */
    @SerialName("exp") val expirationTime: Long,

    /**
     * Integer timestamp, measured in the number of seconds since January 1 1970 UTC, indicating when this token was
     * originally issued, as defined in JWT [https://datatracker.ietf.org/doc/html/rfc7519].
     */
    @SerialName("iat") val issuedAt: Long,

    /**
     * Integer timestamp, measured in the number of seconds since January 1 1970 UTC, indicating when this token is not
     * to be used before, as defined in JWT [https://datatracker.ietf.org/doc/html/rfc7519].
     */
    @SerialName("nbf") val notBefore: Long,

    /**
     * Usually a machine-readable identifier of the resource owner who authorized this token.
     */
    @SerialName("sub") val subject: String,

    /**
     * Extension to enable stating the purpose of the token:
     * - Access Token
     * - Refresh Token
     */
    val purpose: Tokens, // TODO - Decide if we should or implement a further feature to lockdown who can introspect refresh.

) : IntrospectionResponse()

@Serializable
data class InactiveIntrospectionResponse(val active: Boolean = false) : IntrospectionResponse()
