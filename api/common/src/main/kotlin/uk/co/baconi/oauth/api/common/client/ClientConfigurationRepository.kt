package uk.co.baconi.oauth.api.common.client

import uk.co.baconi.oauth.api.common.Repository

interface ClientConfigurationRepository : Repository<ClientConfiguration, ClientId> {
    fun findByClientId(clientId: ClientId): ClientConfiguration? = findById(clientId)
}