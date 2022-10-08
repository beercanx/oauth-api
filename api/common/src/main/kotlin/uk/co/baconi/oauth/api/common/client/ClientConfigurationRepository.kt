package uk.co.baconi.oauth.api.common.client

import com.typesafe.config.ConfigFactory
import io.ktor.server.config.*
import uk.co.baconi.oauth.api.common.Repository
import uk.co.baconi.oauth.api.common.scope.Scope

class ClientConfigurationRepository private constructor(
    private val clientConfiguration: Map<ClientId, ClientConfiguration>
) : Repository<ClientConfiguration, ClientId> {

    constructor() : this(loadClientConfiguration())

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

        private val baseConfig = ConfigFactory.load()
        private val clientIds = baseConfig.getObject("uk.co.baconi.oauth.api.client.configuration").keys.map(::ClientId)
        private val clientConfiguration = baseConfig.getConfig("uk.co.baconi.oauth.api.client.configuration")

        private fun loadClientConfiguration(): Map<ClientId, ClientConfiguration> {
            return clientIds.associateWith { clientId ->
                val config = clientConfiguration.getConfig(clientId.value)
                ClientConfiguration(
                    id = clientId,
                    type = config
                        .getString("type")
                        .let(ClientType::fromValue),
                    redirectUris = config
                        .tryGetStringList("redirectUrls")
                        ?.filter(String::isNotBlank)
                        ?.map(String::trim)
                        ?.toSet()
                        ?: emptySet(),
                    allowedScopes = config
                        .tryGetStringList("allowedScopes")
                        ?.map(Scope::fromValue)
                        ?.toSet()
                        ?: emptySet()
                )
            }
        }
    }
}