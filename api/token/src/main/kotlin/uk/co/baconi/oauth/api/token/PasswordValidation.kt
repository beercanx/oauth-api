package uk.co.baconi.oauth.api.token

import io.ktor.http.*
import uk.co.baconi.oauth.api.common.client.ClientPrincipal
import uk.co.baconi.oauth.api.common.client.ConfidentialClient
import uk.co.baconi.oauth.api.common.grant.GrantType.Password
import uk.co.baconi.oauth.api.common.scope.ScopeRepository
import uk.co.baconi.oauth.api.common.scope.ScopesDeserializer
import uk.co.baconi.oauth.api.token.TokenErrorType.*
import uk.co.baconi.oauth.api.token.TokenRequest.Invalid

private const val USERNAME = "username"
private const val PASSWORD = "password"
private const val SCOPE = "scope"

interface PasswordValidation {

    val scopeRepository: ScopeRepository

    fun validatePasswordRequest(parameters: Parameters, client: ClientPrincipal): TokenRequest {

        val username = parameters[USERNAME]
        val password = parameters[PASSWORD]?.toCharArray()
        val rawScopes = parameters[SCOPE]?.let(ScopesDeserializer::deserialize) ?: emptySet()
        val scopes = rawScopes.mapNotNull(scopeRepository::findById).toSet()

        return when {
            client !is ConfidentialClient -> Invalid(UnauthorizedClient, "not authorized to: ${Password.value}")
            !client.can(Password) -> Invalid(UnauthorizedClient, "not authorized to: ${Password.value}")

            username == null -> Invalid(InvalidRequest, "missing parameter: $USERNAME")
            username.isBlank() -> Invalid(InvalidRequest, "invalid parameter: $USERNAME")

            password == null -> Invalid(InvalidRequest, "missing parameter: $PASSWORD")
            // As long as the password field is present we should not restrict what it contains.

            // The requested scope is invalid, unknown, or malformed.
            parameters[SCOPE] != null && scopes.isEmpty() -> Invalid(InvalidScope, "invalid parameter: $SCOPE")

            // Check that the Client cannot be issued one of the scopes
            rawScopes.size != scopes.size -> Invalid(InvalidScope, "invalid parameter: $SCOPE")
            !scopes.all(client::canBeIssued) -> Invalid(InvalidScope, "invalid parameter: $SCOPE")

            else -> PasswordRequest(client, scopes, username, password)
        }
    }
}