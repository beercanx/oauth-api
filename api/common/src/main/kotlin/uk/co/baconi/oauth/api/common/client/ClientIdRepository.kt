package uk.co.baconi.oauth.api.common.client

import uk.co.baconi.oauth.api.common.Repository

interface ClientIdRepository : Repository<ClientId, String> {
    fun findAll(): Sequence<ClientId>
}