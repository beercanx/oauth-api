package uk.co.baconi.oauth.api.common.client

import java.util.UUID

data class ClientSecret(val id: UUID, val clientId: ClientId, internal val hashedSecret: String) {

    /**
     * Generated based on its database ID field [id].
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ClientSecret

        if (id != other.id) return false

        return true
    }

    /**
     * Generated based on its database ID field [id].
     */
    override fun hashCode(): Int {
        return id.hashCode()
    }

    /**
     * Generated to exclude [hashedSecret] from the toString output.
     */
    override fun toString(): String {
        return "ClientSecret(id=$id, clientId=$clientId, hashedSecret='REDACTED')"
    }
}