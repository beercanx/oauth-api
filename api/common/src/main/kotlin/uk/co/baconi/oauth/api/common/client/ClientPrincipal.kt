package uk.co.baconi.oauth.api.common.client

import io.ktor.server.auth.*
import uk.co.baconi.oauth.api.common.client.ClientAction.Introspect
import uk.co.baconi.oauth.api.common.client.ClientAction.ProofKeyForCodeExchange
import uk.co.baconi.oauth.api.common.grant.GrantType
import uk.co.baconi.oauth.api.common.grant.GrantType.AuthorisationCode
import uk.co.baconi.oauth.api.common.grant.GrantType.Password
import uk.co.baconi.oauth.api.common.scope.Scope

sealed class ClientPrincipal : Principal {
    abstract val id: ClientId
    abstract val configuration: ClientConfiguration
    fun can(action: ClientAction): Boolean = configuration.allowedActions.contains(action)
    fun can(grantType: GrantType): Boolean = configuration.allowedGrantTypes.contains(grantType)
    fun canBeIssued(scope: Scope): Boolean = configuration.allowedScopes.contains(scope)
    fun hasRedirectUri(redirectUri: String): Boolean = configuration.redirectUris.contains(redirectUri)

    companion object {
        fun fromConfiguration(configuration: ClientConfiguration) = when(configuration.type) {
            ClientType.Public -> PublicClient(configuration)
            ClientType.Confidential -> ConfidentialClient(configuration)
        }
    }
}

data class ConfidentialClient(override val configuration: ClientConfiguration) : ClientPrincipal() {

    override val id: ClientId = configuration.id

    init {
        require(configuration.type == ClientType.Confidential) { "[$id] type cannot be [${configuration.type}]" }
    }

    override fun toString(): String = "ConfidentialClient(id=$id, configuration=REDACTED)"
}

data class PublicClient(override val configuration: ClientConfiguration) : ClientPrincipal() {

    override val id: ClientId = configuration.id

    init {
        require(configuration.type == ClientType.Public) { "[$id] type cannot be [${configuration.type}]" }
        require(!can(Introspect)) { "public clients must not be allowed to introspect: $id" }
        require(!can(Password)) { "public clients must not use password grant: $id" }
        require(!(can(AuthorisationCode) && !can(ProofKeyForCodeExchange))) {
            "public clients must not use authorisation code grant without PKCE: $id"
        }
    }

    override fun toString(): String = "PublicClient(id=$id, configuration=REDACTED)"
}
