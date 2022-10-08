package uk.co.baconi.oauth.api.common.client

import io.ktor.server.config.*

class ClientIdRepositoryConfig internal constructor(private val clientIds: Map<String, ClientId>) : ClientIdRepository {

    constructor(config: ApplicationConfig) : this(loadClientIds(config))

    companion object {
        private fun loadClientIds(config: ApplicationConfig) = config
            .property("client.clientIds")
            .getList()
            .map(String::trim)
            .filter(String::isNotEmpty)
            .associateWith { key -> ClientId(key) }
    }

    override fun findById(id: String): ClientId? {
        return clientIds[id]
    }

    override fun findAll(): Sequence<ClientId> {
        return clientIds.values.asSequence()
    }

}