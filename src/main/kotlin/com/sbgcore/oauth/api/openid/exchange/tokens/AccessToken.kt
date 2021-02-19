package com.sbgcore.oauth.api.openid.exchange.tokens

import com.sbgcore.oauth.api.openid.ClientId
import com.sbgcore.oauth.api.openid.Scopes
import org.dizitart.no2.IndexType.NonUnique
import org.dizitart.no2.IndexType.Unique
import org.dizitart.no2.objects.Id
import org.dizitart.no2.objects.Index
import org.dizitart.no2.objects.Indices
import java.time.OffsetDateTime
import java.util.*

@Indices(
    Index("value", type = Unique),         // To support the day to day look up of an access token
    Index("customerId", type = NonUnique), // To support finding all access tokens for a specific customer
    Index("username", type = NonUnique),   // To support finding all access tokens for a specific customer
    Index("clientId", type = NonUnique)    // To support purging access tokens by client [decommissioned client || compromised client]
)
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
