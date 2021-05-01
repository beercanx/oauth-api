package uk.co.baconi.oauth.api.tokens

import uk.co.baconi.oauth.api.Repository
import uk.co.baconi.oauth.api.client.ClientId
import java.util.*

/**
 * A [Repository] for storing [AccessToken]'s
 */
interface AccessTokenRepository : Repository<AccessToken, UUID> {

    /**
     * Find an [AccessToken] based on its value.
     */
    fun findByValue(value: UUID): AccessToken?

    /**
     * Find an [AccessToken] based on its value.
     */
    fun findByValue(value: String): AccessToken?

    /**
     * Find all the [AccessToken]'s issued for a given username.
     */
    fun findAllByUsername(username: String): Set<AccessToken>

    /**
     * Find all the [AccessToken]'s issued to a given client.
     */
    fun findAllByClientId(clientId: ClientId): Set<AccessToken>

}