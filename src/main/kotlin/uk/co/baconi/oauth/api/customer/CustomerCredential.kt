package uk.co.baconi.oauth.api.customer

import org.dizitart.no2.objects.Id

data class CustomerCredential(
    @Id val username: String,
    val secret: String,
    val temporary: Boolean = false,
    val locked: Boolean = false
) {

    /**
     * Generated to exclude [secret] from the toString output.
     */
    override fun toString(): String {
        return "CustomerCredential(username='$username', secret='REDACTED', temporary=$temporary, locked=$locked)"
    }

    /**
     * Generated based on its database ID field [username].
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

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
