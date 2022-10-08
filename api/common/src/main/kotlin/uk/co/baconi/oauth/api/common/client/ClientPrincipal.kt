package uk.co.baconi.oauth.api.common.client

import io.ktor.server.auth.*
import uk.co.baconi.oauth.api.common.scope.Scope

sealed class ClientPrincipal : Principal {
    abstract val id: ClientId
    abstract val configuration: ClientConfiguration
    fun canBeIssued(scope: Scope): Boolean = configuration.allowedScopes.contains(scope)
    fun hasRedirectUri(redirectUri: String): Boolean = configuration.redirectUris.contains(redirectUri)
}

data class ConfidentialClient(override val configuration: ClientConfiguration) : ClientPrincipal() {

    override val id: ClientId = configuration.id

    init {
        require(configuration.type == ClientType.Confidential) { "type cannot be [${configuration.type}]" }
    }
}

data class PublicClient(override val configuration: ClientConfiguration) : ClientPrincipal() {

    override val id: ClientId = configuration.id

    init {
        require(configuration.type == ClientType.Public) { "type cannot be [${configuration.type}]" }
    }
}
