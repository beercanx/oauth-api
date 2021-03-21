package com.sbgcore.oauth.api.tokens

import com.sbgcore.oauth.api.openid.ClientId
import com.sbgcore.oauth.api.openid.Scopes
import java.time.OffsetDateTime
import java.util.*

interface Token {

    /**
     * The id of the issued access token, here because its believed we'll need to do relationships at some point.
     */
    val id: UUID

    /**
     * The actual value of the token, consumers would see this as the access_token value.
     */
    val value: UUID

    /**
     * The machine readable identifier, for the subject of this token.
     */
    val customerId: Long

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