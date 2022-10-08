package uk.co.baconi.oauth.api.common.client

import uk.co.baconi.oauth.api.common.Repository

interface ClientIdRepository : Repository<ClientId, String> {

    /**
     * Find all the [ClientId]s
     */
    fun findAll(): Sequence<ClientId>

}