package uk.co.baconi.oauth.api.authentication

import kotlinx.serialization.Serializable

sealed class AuthenticationRequest {

    @Serializable
    class Raw(val username: String?, val password: CharArray?, val csrfToken: String?) {
        override fun toString() = "Raw(username='$username', password='REDACTED', csrfToken='$csrfToken')"
    }

    class InvalidField(val name: String) : AuthenticationRequest() {
        override fun toString() = "InvalidField(name='$name')"
    }

    class Valid(val username: String, val password: CharArray) : AuthenticationRequest() {
        override fun toString() = "Valid(username='$username', password='REDACTED')"
    }
}