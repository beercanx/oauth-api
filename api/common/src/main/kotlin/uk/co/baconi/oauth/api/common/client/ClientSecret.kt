package uk.co.baconi.oauth.api.common.client

data class ClientSecret(val id: Long, val clientId: ClientId, val secret: String) {

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
     * Generated to exclude [secret] from the toString output.
     */
    override fun toString(): String {
        return "ClientSecret(id=$id, clientId=$clientId, secret='REDACTED')"
    }
}