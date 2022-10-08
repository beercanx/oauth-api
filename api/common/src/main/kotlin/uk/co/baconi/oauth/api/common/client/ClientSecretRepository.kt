package uk.co.baconi.oauth.api.common.client

import uk.co.baconi.oauth.api.common.Repository

/**
 * A [Repository] for storing [ClientSecret]'s
 */
interface ClientSecretRepository : Repository<ClientSecret, Long> {

    // TODO - Implement with TypesafeConfig or a database

    /**
     * Find all the [ClientSecret]'s issued to a given [ClientId].
     */
    fun findAllByClientId(clientId: ClientId): Sequence<ClientSecret>

    /**
     * Find all the [ClientSecret]'s issued to a given [ClientId],
     * but takes a [String] and looks up the [ClientId] first.
     */
    fun findAllByClientId(clientId: String): Sequence<ClientSecret>

}