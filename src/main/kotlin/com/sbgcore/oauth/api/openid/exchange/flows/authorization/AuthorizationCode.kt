package com.sbgcore.oauth.api.openid.exchange.flows.authorization

import io.ktor.http.*
import org.dizitart.no2.objects.Id
import java.time.OffsetDateTime
import java.util.*

// TODO - Expand with consumer issued to details
data class AuthorizationCode(

    // The actual code
    @Id val value: UUID,

    // Used to calculate when it expires?
    val issuedAt: OffsetDateTime,

    // Added because we need to validate on exchange its the same url as stated in https://tools.ietf.org/html/rfc6749#section-4.1.3
    val redirectUrl: Url
)
