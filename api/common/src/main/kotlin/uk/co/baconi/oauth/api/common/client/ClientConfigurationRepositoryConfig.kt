package uk.co.baconi.oauth.api.common.client

import io.ktor.server.config.*
import uk.co.baconi.oauth.api.common.scope.Scope

class ClientConfigurationRepositoryConfig internal constructor(
    private val clientConfiguration: Map<ClientId, ClientConfiguration>
) : ClientConfigurationRepository {

    constructor(
        config: ApplicationConfig,
        clientIdRepository: ClientIdRepository
    ) : this(
        clientIdRepository
            .findAll()
            .map { clientId ->
                clientId to config.config("client.configuration").config(clientId.value)
            }
            .map { (clientId, config) ->
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
            .toMap()
    )

    override fun findById(id: ClientId): ClientConfiguration? {
        return clientConfiguration[id]
    }
}