package uk.co.baconi.oauth.common.authentication

import kotlinx.serialization.Serializable

@Serializable
data class CustomerAuthenticationRequest(val username: String, val password: CharArray) {

    /**
     * Generated to redact [password] from the output
     */
    override fun toString(): String {
        return "CustomerAuthenticationRequest(username='$username', password='REDACTED')"
    }

    /**
     * Generated because of how arrays and the auto generated equals/hashCode methods of data classes work.
     * https://rules.sonarsource.com/kotlin/RSPEC-6218
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as CustomerAuthenticationRequest

        if (username != other.username) return false
        if (!password.contentEquals(other.password)) return false

        return true
    }

    /**
     * Generated because of how arrays and the auto generated equals/hashCode methods of data classes work.
     * https://rules.sonarsource.com/kotlin/RSPEC-6218
     */
    override fun hashCode(): Int {
        var result = username.hashCode()
        result = 31 * result + password.contentHashCode()
        return result
    }
}
