package uk.co.baconi.oauth.api.common.client

import uk.co.baconi.oauth.api.common.grant.GrantType
import uk.co.baconi.oauth.api.common.scope.Scope

data class ClientConfiguration(
    val id: ClientId,
    val type: ClientType,
    val redirectUris: Set<String>,
    val allowedScopes: Set<Scope>,
    val allowedActions: Set<ClientAction>,
    val allowedGrantTypes: Set<GrantType>
) {
    val isConfidential = type == ClientType.Confidential
    val isPublic = type == ClientType.Public
}
