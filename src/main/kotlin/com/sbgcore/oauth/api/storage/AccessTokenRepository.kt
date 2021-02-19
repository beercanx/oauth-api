package com.sbgcore.oauth.api.storage

import com.sbgcore.oauth.api.openid.ClientId
import com.sbgcore.oauth.api.openid.exchange.tokens.AccessToken
import java.util.UUID

interface AccessTokenRepository : Repository<AccessToken, UUID> {

    /**
     * Find an [AccessToken] based on its value.
     */
    fun findByValue(value: UUID): AccessToken?

    /**
     * Find all the [AccessToken]'s issued for a given customer id.
     */
    fun findAllByCustomerId(customerId: Long): Set<AccessToken>

    /**
     * Find all the [AccessToken]'s issued for a given username.
     */
    fun findAllByUsername(username: String): Set<AccessToken>

    /**
     * Find all the [AccessToken]'s issued to a given client.
     */
    fun findAllByClient(clientId: ClientId): Set<AccessToken>

}