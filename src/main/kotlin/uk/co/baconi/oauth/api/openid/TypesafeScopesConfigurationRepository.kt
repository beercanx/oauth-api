package uk.co.baconi.oauth.api.openid

import uk.co.baconi.oauth.api.client.TypesafeClientConfigurationRepository
import uk.co.baconi.oauth.api.enums.deserialise
import uk.co.baconi.oauth.api.enums.serialise
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
        ConfigFactory.load().getConfig("uk.co.baconi.oauth.api.scopes")
    )

    override fun insert(new: ScopesConfiguration) = error("Insert operation is not supported")

    override fun delete(id: Scopes) = error("Delete operation is not supported")

    /**
     * @throws ConfigException.Missing if a scope config value is absent or null
     * @throws ConfigException.BadValue if a scope config value is not convertible to required type
     * @throws ConfigException.WrongType if a scope config value is not convertible to required type
     */
    override fun findById(id: Scopes): ScopesConfiguration? {
        val scopesValue = serialise(id)
        return if (repository.hasPath(scopesValue)) {
            val config = repository.getConfig(scopesValue)
            ScopesConfiguration(
                id = id,
                claims = config.tryGetStringList("claims").toClaims()
            )
        } else {
            null
        }
    }

    private fun List<String>?.toClaims(): Set<Claims> {
        return this?.mapNotNull{claims -> deserialise<Claims>(claims)}?.toSet() ?: emptySet()
    }
}