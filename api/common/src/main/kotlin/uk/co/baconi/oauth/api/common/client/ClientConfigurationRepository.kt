package uk.co.baconi.oauth.api.common.client

import io.ktor.server.config.*
import uk.co.baconi.oauth.api.common.Repository
import uk.co.baconi.oauth.api.common.scope.Scope

class ClientConfigurationRepository internal constructor(
    private val clientConfiguration: Map<ClientId, ClientConfiguration>
) : Repository<ClientConfiguration, ClientId> {

    constructor(config: ApplicationConfig) : this(loadClientConfiguration(config))

    /**
     * Find the [ClientConfiguration] for the given [ClientId].
     */
    override fun findById(id: ClientId): ClientConfiguration? {
        return clientConfiguration[id]
    }

    /**
     * Find the [ClientConfiguration] for the given [clientId].
     */
    fun findByClientId(clientId: String): ClientConfiguration? {
        return clientConfiguration[ClientId(clientId)]
    }

    /**
     * Find all the [ClientId]s.
     */
    fun findAllClientIds(): Sequence<ClientId> {
        return clientConfiguration.keys.asSequence()
    }

    companion object {
        // TODO - Replace with TypesafeConfig
        private fun loadClientConfiguration(config: ApplicationConfig): Map<ClientId, ClientConfiguration> {
            return config.config("uk.co.baconi.oauth.api.client.configuration")
                .keys()
                .map { clientId ->
                    ClientId(clientId) to config.config("uk.co.baconi.oauth.api.client.configuration").config(clientId)
                }.associate { (clientId, config) ->
                    clientId to ClientConfiguration(
                        id = clientId,
                        type = config
                            .property("type")
                            .getString()
                            .let(ClientType::fromValue),
                        redirectUris = config
                            .property("redirectUrls")
                            .getList()
                            .filter(String::isNotBlank)
                            .map(String::trim)
                            .toSet(),
                        allowedScopes = config
                            .property("allowedScopes")
                            .getList()
                            .map(Scope::fromValue)
                            .toSet()
                    )
                }
        }
    }
}