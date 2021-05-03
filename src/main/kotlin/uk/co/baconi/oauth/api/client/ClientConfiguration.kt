package uk.co.baconi.oauth.api.client

import io.ktor.http.*
import org.dizitart.no2.objects.Id
import uk.co.baconi.oauth.api.openid.Scopes

data class ClientConfiguration(
    @Id val id: ClientId,
    val type: ClientType,
    val redirectUrls: Set<Url>,
    val allowedScopes: Set<Scopes>
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
