package uk.co.baconi.oauth.api.client

import uk.co.baconi.oauth.api.enums.deserialise
import uk.co.baconi.oauth.api.enums.serialise
import uk.co.baconi.oauth.api.openid.Scopes
import com.typesafe.config.Config
import com.typesafe.config.ConfigException
import com.typesafe.config.ConfigFactory
import io.ktor.config.*
import io.ktor.http.*

/**
 * A static Typesafe Config implementation of the [ClientConfigurationRepository].
 */
class TypesafeClientConfigurationRepository internal constructor(
    private val repository: Config
) : ClientConfigurationRepository {

    /**
     * Create a new instance of [TypesafeClientConfigurationRepository] with an isolated [Config] instance.
     */
    constructor() : this(
        ConfigFactory.load().getConfig("uk.co.baconi.oauth.api.clients")
    )

    override fun insert(new: ClientConfiguration) = error("Insert operation is not supported")

    override fun delete(id: ClientId) = error("Delete operation is not supported")

    /**
     * @throws ConfigException.Missing if a client config value is absent or null
     * @throws ConfigException.BadValue if a client config value is not convertible to required type
     * @throws ConfigException.WrongType if a client config value is not convertible to required type
     */
    override fun findById(id: ClientId): ClientConfiguration? {
        val clientIdValue = serialise(id)
        return if (repository.hasPath(clientIdValue)) {
            val config = repository.getConfig(clientIdValue)
            ClientConfiguration(
                id = id,
                type = config.getEnum(ClientType::class.java, "type"),
                redirectUrls = config.tryGetStringList("redirectUrls").toUrls(),
                allowedScopes = config.tryGetStringList("allowedScopes").toScopes(),
            )
        } else {
            null
        }
    }

    private fun List<String>?.toUrls(): Set<Url> {
        return this?.map(::Url)?.toSet() ?: emptySet()
    }

    private fun List<String>?.toScopes(): Set<Scopes> {
        return this?.mapNotNull{scope -> deserialise<Scopes>(scope)}?.toSet() ?: emptySet()
    }
}
