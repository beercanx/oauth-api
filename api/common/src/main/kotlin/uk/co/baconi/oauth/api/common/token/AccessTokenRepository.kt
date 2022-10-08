package uk.co.baconi.oauth.api.common.token

import uk.co.baconi.oauth.api.common.Repository
import uk.co.baconi.oauth.api.common.Repository.*
import uk.co.baconi.oauth.api.common.client.ClientId

/**
 * A [Repository] for storing [AccessToken]'s
 */
interface AccessTokenRepository : Repository<AccessToken, String>, WithInsert<AccessToken>, WithDelete<AccessToken, String> {

    // TODO - Implement a in a database

    /**
     * Find an [AccessToken] based on its value.
     */
    fun findByValue(value: String): AccessToken? = findById(value)

    /**
     * Find all the [AccessToken]'s issued for a given username.
     */
    fun findAllByUsername(username: String): Sequence<AccessToken>

    /**
     * Find all the [AccessToken]'s issued to a given client.
     */
    fun findAllByClientId(clientId: ClientId): Sequence<AccessToken>

}