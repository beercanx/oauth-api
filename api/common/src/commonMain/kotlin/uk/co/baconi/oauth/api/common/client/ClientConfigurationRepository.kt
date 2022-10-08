package uk.co.baconi.oauth.api.common.client

import uk.co.baconi.oauth.api.common.Repository

interface ClientConfigurationRepository : Repository<ClientConfiguration, ClientId> {

    val clientIdRepository: ClientIdRepository

    fun findByClientId(clientId: ClientId): ClientConfiguration? = findById(clientId)

    fun findByClientId(clientId: String): ClientConfiguration? {
        val clientIdentifier = clientIdRepository.findById(clientId)
        return if (clientIdentifier == null) {
            null
        } else {
            findById(clientIdentifier)
        }
    }
}