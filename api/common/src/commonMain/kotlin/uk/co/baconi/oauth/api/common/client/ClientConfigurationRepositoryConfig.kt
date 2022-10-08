package uk.co.baconi.oauth.api.common.client

import io.ktor.server.config.*
import uk.co.baconi.oauth.api.common.scope.ScopeRepository

class ClientConfigurationRepositoryConfig internal constructor(
    override val clientIdRepository: ClientIdRepository,
    private val clientConfiguration: Map<ClientId, ClientConfiguration>
) : ClientConfigurationRepository {

    constructor(
        config: ApplicationConfig,
        scopeRepository: ScopeRepository,
        clientIdRepository: ClientIdRepository
    ) : this(
        clientIdRepository,
        loadClientConfiguration(config.config("client.configuration"), scopeRepository, clientIdRepository)
    )

    companion object {

        private fun loadClientConfiguration(
            config: ApplicationConfig,
            scopeRepository: ScopeRepository,
            clientIdRepository: ClientIdRepository,
        ): Map<ClientId, ClientConfiguration> {
            return clientIdRepository
                .findAll()
                .map { clientId ->
                    clientId to config.config(clientId.value)
                }
                .map { (clientId, config) ->
                    clientId to ClientConfiguration(
                        id = clientId,
                        type = config
                            .property("type")
                            .getString()
                            .let(::enumValueOf),
                        redirectUris = config
                            .property("redirectUrls")
                            .getList()
                            .toSet(),
                        allowedScopes = config
                            .property("allowedScopes")
                            .getList()
                            .mapNotNull(scopeRepository::findById)
                            .toSet(),
                    )
                }
                .toMap()
        }
    }

    override fun findById(id: ClientId): ClientConfiguration? {
        return clientConfiguration[id]
    }

}