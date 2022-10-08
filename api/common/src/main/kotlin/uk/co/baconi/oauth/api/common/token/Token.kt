package uk.co.baconi.oauth.api.common.token

import uk.co.baconi.oauth.common.authentication.AuthenticatedUsername
import uk.co.baconi.oauth.api.common.client.ClientId
import uk.co.baconi.oauth.api.common.scope.Scope
import java.time.Instant
import java.util.*

interface Token {

    /**
     * The actual value of the token, consumers would see this as the access_token value.
     */
    val value: UUID

    /**
     * The human-readable identifier, for the subject of this token.
     */
    val username: AuthenticatedUsername

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
    val issuedAt: Instant

    /**
     * The date and time indicating when this token will expire.
     */
    val expiresAt: Instant

    /**
     * The date and time indicating when this token is not to be used before.
     */
    val notBefore: Instant
}