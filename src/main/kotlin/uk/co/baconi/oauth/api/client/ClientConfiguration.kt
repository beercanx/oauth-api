package uk.co.baconi.oauth.api.client

import org.dizitart.no2.objects.Id
import uk.co.baconi.oauth.api.authorisation.AuthorisationResponseType
import uk.co.baconi.oauth.api.scopes.Scopes

data class ClientConfiguration(
    @Id val id: ClientId,
    val type: ClientType,
    val redirectUrls: Set<String>,
    val allowedScopes: Set<Scopes>,
    val allowedResponseTypes: Set<AuthorisationResponseType>
) {

    val isConfidential = type == ClientType.Confidential
    val isPublic = type == ClientType.Public

    /**
     * Generated based on its database ID field [id].
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

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
