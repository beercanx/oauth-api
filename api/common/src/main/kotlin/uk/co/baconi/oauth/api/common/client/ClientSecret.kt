package uk.co.baconi.oauth.api.common.client

import java.util.*

data class ClientSecret(val id: UUID, val clientId: ClientId, internal val hashedSecret: String) {
    /**
     * Generated to exclude [hashedSecret] from the toString output.
     */
    override fun toString(): String {
        return "ClientSecret(id=$id, clientId=$clientId, hashedSecret='REDACTED')"
    }
}