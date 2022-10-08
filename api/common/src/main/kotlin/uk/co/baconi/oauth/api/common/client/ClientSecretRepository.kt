package uk.co.baconi.oauth.api.common.client

import com.typesafe.config.ConfigFactory
import java.util.*

class ClientSecretRepository {

    private val clientSecrets: Map<ClientId, List<ClientSecret>> = loadClientSecrets()

    fun findById(id: UUID): ClientSecret? {
        return clientSecrets.values.flatten().firstOrNull { clientSecret -> clientSecret.id == id }
    }

    fun findAllByClientId(clientId: ClientId): Sequence<ClientSecret> {
        return clientSecrets[clientId]?.asSequence() ?: emptySequence()
    }

    fun findAllByClientId(clientId: String): Sequence<ClientSecret> {
        return findAllByClientId(ClientId(clientId))
    }

    companion object {

        private val baseConfig = ConfigFactory.load()
        private val clientIds = baseConfig.getObject("uk.co.baconi.oauth.api.client.secrets").keys.map(::ClientId)
        private val clientSecrets = baseConfig.getConfig("uk.co.baconi.oauth.api.client.secrets")

        private fun loadClientSecrets(): Map<ClientId, List<ClientSecret>> {
            return clientIds.associateWith { clientId ->
                clientSecrets.getObject(clientId.value).keys.map { id ->
                    ClientSecret(
                        id = UUID.fromString(id),
                        clientId = clientId,
                        hashedSecret = clientSecrets.getConfig(clientId.value).getString(id)
                    )
                }
            }
        }
    }
}