package uk.co.baconi.oauth.api.common.client

import uk.co.baconi.oauth.api.common.scope.Scope

data class ClientConfiguration(
    val id: ClientId,
    val type: ClientType,
    val redirectUris: Set<String>,
    val allowedScopes: Set<Scope>,
    //val allowedResponseTypes: Set<AuthorisationResponseType>
) {

    val isConfidential = type == ClientType.Confidential
    val isPublic = type == ClientType.Public

    /**
     * Generated based on its database ID field [id].
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ClientConfiguration

        if (id != other.id) return false

        return true
    }

    /**
     * Generated based on its database ID field [id].
     */
    override fun hashCode(): Int {
        return id.hashCode()
    }
}
