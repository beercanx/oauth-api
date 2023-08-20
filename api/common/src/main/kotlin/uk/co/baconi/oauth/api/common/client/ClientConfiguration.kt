package uk.co.baconi.oauth.api.common.client

import uk.co.baconi.oauth.api.common.grant.GrantType
import uk.co.baconi.oauth.api.common.scope.Scope

data class ClientConfiguration(
    val id: ClientId,
    val type: ClientType,
    val redirectUris: Set<String>,
    val allowedScopes: Set<Scope>, // TODO - Will need to reconsider if ScopeRepository is dynamic rather than static.
    val allowedActions: Set<ClientAction>,
    val allowedGrantTypes: Set<GrantType>,
) {
    val isConfidential = type == ClientType.Confidential
    val isPublic = type == ClientType.Public

    // TODO - Should the check for redirectUri.isAbsoluteURI be in here like we do for ClientPrincipal?
}
