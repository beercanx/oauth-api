package uk.co.baconi.oauth.api.client

import io.ktor.server.auth.*
import uk.co.baconi.oauth.api.scopes.Scopes

sealed class ClientPrincipal : Principal {
    abstract val id: ClientId
    abstract val configuration: ClientConfiguration
    fun canBeIssued(scope: Scopes): Boolean = configuration.allowedScopes.contains(scope)
    fun hasRedirectUri(redirectUri: String): Boolean = configuration.redirectUris.contains(redirectUri)
}

data class ConfidentialClient(
    override val configuration: ClientConfiguration
) : ClientPrincipal() {

    override val id: ClientId = configuration.id

    init {
        require(configuration.type == ClientType.Confidential) { "type cannot be [${configuration.type}]" }
    }
}

data class PublicClient(
    override val configuration: ClientConfiguration
) : ClientPrincipal() {

    override val id: ClientId = configuration.id

    init {
        require(configuration.type == ClientType.Public) { "type cannot be [${configuration.type}]" }
    }
}
