package uk.co.baconi.oauth.api.common.customer

data class CustomerCredential(
    val username: String,
    internal val hashedSecret: String,
    // TODO - Include created_at: Instant,
    // TODO - Include modified_at: Instant,
) {

    /**
     * Generated to exclude [hashedSecret] from the toString output.
     */
    override fun toString(): String {
        return "CustomerCredential(username='$username', hashedSecret='REDACTED')"
    }

    /**
     * Generated based on its database ID field [username].
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as CustomerCredential

        if (username != other.username) return false

        return true
    }

    /**
     * Generated based on its database ID field [username].
     */
    override fun hashCode(): Int {
        return username.hashCode()
    }
}
