package uk.co.baconi.oauth.api.common.client

import com.typesafe.config.ConfigFactory
import uk.co.baconi.oauth.api.common.Repository
import java.util.*

/**
 * A [Repository] for retrieving [ClientSecret]'s
 */
class ClientSecretRepository : Repository<ClientSecret, UUID> {

    private val clientSecrets: Map<ClientId, List<ClientSecret>> = loadClientSecrets()

    /**
     * Find the [ClientSecret] for the given [UUID] or null.
     */
    override fun findById(id: UUID): ClientSecret? {
        return clientSecrets.values.flatten().firstOrNull { clientSecret -> clientSecret.id == id }
    }

    /**
     * Find all the [ClientSecret]'s issued to a given [ClientId].
     */
    fun findAllByClientId(clientId: ClientId): Sequence<ClientSecret> {
        return clientSecrets[clientId]?.asSequence() ?: emptySequence()
    }

    /**
     * Find all the [ClientSecret]'s issued to a given [ClientId],
     * but takes a [String] and looks up the [ClientId] first.
     */
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