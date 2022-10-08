package uk.co.baconi.oauth.api.common.customer

data class CustomerStatus(
    val username: String,
    val state: CustomerState,
    // TODO - Include created_at
    // TODO - Include modified_at
) {

    /**
     * Generated based on its database ID field [username].
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as CustomerStatus

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
