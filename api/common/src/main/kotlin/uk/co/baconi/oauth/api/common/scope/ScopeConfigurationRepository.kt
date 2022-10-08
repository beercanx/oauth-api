package uk.co.baconi.oauth.api.common.scope

import com.typesafe.config.ConfigFactory
import uk.co.baconi.oauth.api.common.claim.Claim

class ScopeConfigurationRepository {

    private val clientConfiguration: Map<Scope, ScopeConfiguration> = loadScopeConfiguration()

    fun findById(id: Scope): ScopeConfiguration? {
        return clientConfiguration[id]
    }

    companion object {

        private val scopeConfigurations = ConfigFactory.load().getConfig("uk.co.baconi.oauth.api.scope.configuration")

        private fun loadScopeConfiguration(): Map<Scope, ScopeConfiguration> {
            return enumValues<Scope>()
                .filter { scope -> scopeConfigurations.hasPath(""""${scope.value}"""") }
                .associateWith { scope ->
                    val config = scopeConfigurations.getConfig(""""${scope.value}"""")
                    ScopeConfiguration(
                        id = scope,
                        claims = config
                            .getStringList("claims")
                            .map(Claim::fromValue)
                            .toSet()
                    )
                }
        }
    }
}