package uk.co.baconi.oauth.api.common.scope

import com.typesafe.config.ConfigFactory

class ScopeRepository {

    private val clientConfiguration: Map<String, Scope?> = loadScopes()

    fun findById(id: String): Scope? {
        return clientConfiguration[id]
    }

    companion object {

        private var _maxScopeFieldLength: Int? = null

        /**
         * Assuming serialisation is via a space delimited string calculate the max length of the scope field.
         * For AccessToken/RefreshToken/AuthorisationCode scope field lengths.
         *
         * TODO - Remove the need for this by actually implementing a relational database setup between scopes and entities.
         */
        val maxScopeFieldLength: Int
            get() = _maxScopeFieldLength ?: throw IllegalStateException("maxScopeFieldLength hasn't been calculated.")

        private val baseConfig = ConfigFactory.load()
        private val scopeKeys = baseConfig.getObject("uk.co.baconi.oauth.api.scopes").keys
        private val scopes = baseConfig.getConfig("uk.co.baconi.oauth.api.scopes")

        private fun loadScopes(): Map<String, Scope?> {
            return scopeKeys.associateWith { scope ->
                val config = scopes.getConfig(""""$scope"""")
                if (config.getBoolean("enabled")) {
                    Scope(scope)
                } else {
                    null
                }
            }.apply {
                val values = values.filterNotNull()
                val gapSize = values.size - 1
                val scopeSize = values.fold(0) { size, scope ->
                    size + scope.value.length
                }
                _maxScopeFieldLength = gapSize + scopeSize
            }
        }
    }
}