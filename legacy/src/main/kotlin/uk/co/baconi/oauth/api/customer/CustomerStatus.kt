package uk.co.baconi.oauth.api.customer

import org.dizitart.no2.objects.Id

data class CustomerStatus(
    @Id val username: String,
    val state: CustomerState
) {

    /**
     * Generated based on its database ID field [username].
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

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
