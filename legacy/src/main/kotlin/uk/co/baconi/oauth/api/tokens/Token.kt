package uk.co.baconi.oauth.api.tokens

import uk.co.baconi.oauth.api.client.ClientId
import uk.co.baconi.oauth.api.scopes.Scopes
import java.time.OffsetDateTime

interface Token {

    /**
     * The actual value of the token, consumers would see this as the access_token value.
     */
    val value: String

    /**
     * The human readable identifier, for the subject of this token.
     */
    val username: String

    /**
     * The if of the oauth client this token was issued to.
     */
    val clientId: ClientId

    /**
     * The scopes associated with this token.
     */
    val scopes: Set<Scopes>

    /**
     * The date and time indicating when this token was originally issued.
     */
    val issuedAt: OffsetDateTime

    /**
     * The date and time indicating when this token will expire.
     */
    val expiresAt: OffsetDateTime

    /**
     * The date and time indicating when this token is not to be used before.
     */
    val notBefore: OffsetDateTime
}