package uk.co.baconi.oauth.common.authentication

import kotlinx.serialization.Serializable

@Serializable
data class CustomerAuthenticationRequest(val username: String, val password: String) {
    /**
     * Generated to redact [password] from the output
     */
    override fun toString(): String {
        return "CustomerAuthenticationRequest(username='$username', password='REDACTED')"
    }
}
