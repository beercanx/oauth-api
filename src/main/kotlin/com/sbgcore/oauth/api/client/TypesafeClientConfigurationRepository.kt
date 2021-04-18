package com.sbgcore.oauth.api.client

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
        ConfigFactory.load().getConfig("com.sbgcore.oauth.api.clients")
    )

    override fun insert(new: ClientConfiguration) = error("Insert operation is not supported")

    override fun delete(id: ClientId) = error("Delete operation is not supported")

    /**
     * @throws ConfigException.Missing if a client config value is absent or null
     * @throws ConfigException.BadValue if a client config value is not convertible to required type
     * @throws ConfigException.WrongType if a client config value is not convertible to required type
     */
    override fun findById(id: ClientId): ClientConfiguration? {
        return if (repository.hasPath(id.value)) {
            val config = repository.getConfig(id.value)
            ClientConfiguration(
                id = id,
                type = config.getEnum(ClientType::class.java, "type"),
                redirectUrls = config.tryGetStringList("redirectUrls")?.map(::Url)?.toSet() ?: emptySet()
            )
        } else {
            null
        }
    }
}
