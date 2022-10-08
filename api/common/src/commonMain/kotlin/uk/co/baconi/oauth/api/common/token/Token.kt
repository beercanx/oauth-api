package uk.co.baconi.oauth.api.common.token

import kotlinx.datetime.LocalDateTime
import uk.co.baconi.oauth.api.common.client.ClientId
import uk.co.baconi.oauth.api.common.scope.Scope

interface Token {

    /**
     * The actual value of the token, consumers would see this as the access_token value.
     */
    val value: String

    /**
     * The human-readable identifier, for the subject of this token.
     */
    val username: String

    /**
     * The if of the oauth client this token was issued to.
     */
    val clientId: ClientId

    /**
     * The scopes associated with this token.
     */
    val scopes: Set<Scope>

    /**
     * The date and time indicating when this token was originally issued.
     */
    val issuedAt: LocalDateTime

    /**
     * The date and time indicating when this token will expire.
     */
    val expiresAt: LocalDateTime

    /**
     * The date and time indicating when this token is not to be used before.
     */
    val notBefore: LocalDateTime
}