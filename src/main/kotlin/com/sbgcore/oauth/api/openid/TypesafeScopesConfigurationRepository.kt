package com.sbgcore.oauth.api.openid

import com.sbgcore.oauth.api.client.TypesafeClientConfigurationRepository
import com.sbgcore.oauth.api.enums.enumByValue
import com.typesafe.config.Config
import com.typesafe.config.ConfigException
import com.typesafe.config.ConfigFactory
import io.ktor.config.*

class TypesafeScopesConfigurationRepository internal constructor(
    private val repository: Config
) : ScopesConfigurationRepository {

    /**
     * Create a new instance of [TypesafeClientConfigurationRepository] with an isolated [Config] instance.
     */
    constructor() : this(
        ConfigFactory.load().getConfig("com.sbgcore.oauth.api.scopes")
    )

    override fun insert(new: ScopesConfiguration) = error("Insert operation is not supported")

    override fun delete(id: Scopes) = error("Delete operation is not supported")

    /**
     * @throws ConfigException.Missing if a scope config value is absent or null
     * @throws ConfigException.BadValue if a scope config value is not convertible to required type
     * @throws ConfigException.WrongType if a scope config value is not convertible to required type
     */
    override fun findById(id: Scopes): ScopesConfiguration? {
        return if (repository.hasPath(id.value)) {
            val config = repository.getConfig(id.value)
            ScopesConfiguration(
                id = id,
                claims = config.tryGetStringList("claims").toClaims()
            )
        } else {
            null
        }
    }

    private fun List<String>?.toClaims(): Set<Claims> {
        return this?.mapNotNull<String, Claims>(::enumByValue)?.toSet() ?: emptySet()
    }
}