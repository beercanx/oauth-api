package uk.co.baconi.oauth.api.client

import uk.co.baconi.oauth.api.Repository
import uk.co.baconi.oauth.api.enums.deserialise

interface ClientConfigurationRepository : Repository<ClientConfiguration, ClientId> {
    fun findByClientId(clientId: ClientId): ClientConfiguration? = findById(clientId)
    fun findByClientId(clientId: String): ClientConfiguration? = deserialise<ClientId>(clientId)?.let(::findById)
}