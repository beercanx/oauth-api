package uk.co.baconi.oauth.api.client

import uk.co.baconi.oauth.api.Repository
import uk.co.baconi.oauth.api.enums.enumByJson

interface ClientConfigurationRepository : Repository<ClientConfiguration, ClientId> {
    fun findByClientId(clientId: ClientId): ClientConfiguration? = findById(clientId)
    fun findByClientId(clientId: String): ClientConfiguration? = enumByJson<ClientId>(clientId)?.let(::findById)
}