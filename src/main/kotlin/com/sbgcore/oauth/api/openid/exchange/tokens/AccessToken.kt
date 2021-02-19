package com.sbgcore.oauth.api.openid.exchange.tokens

import com.sbgcore.oauth.api.openid.ClientId
import com.sbgcore.oauth.api.openid.Scopes
import org.dizitart.no2.objects.Id
import java.time.OffsetDateTime
import java.util.*

data class AccessToken(
    @Id override val id: UUID,
    override val value: UUID,
    override val customerId: Long,
    override val username: String,
    override val clientId: ClientId,
    override val scopes: Set<Scopes>,
    override val issuedAt: OffsetDateTime,
    override val expiresAt: OffsetDateTime,
    override val notBefore: OffsetDateTime
) : Token
