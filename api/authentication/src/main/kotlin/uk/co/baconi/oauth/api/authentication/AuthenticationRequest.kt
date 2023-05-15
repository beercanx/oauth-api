package uk.co.baconi.oauth.api.authentication

sealed class AuthenticationRequest {

    data class Raw(val username: String?, val password: String?, val csrfToken: String?) {
        override fun toString() = "Raw(username='$username', password='REDACTED', csrfToken='$csrfToken')"
    }

    data class InvalidField(val name: String) : AuthenticationRequest()

    data class Valid(val username: String, val password: CharArray, val csrfToken: String) : AuthenticationRequest() {

        /**
         * Generated to redact [password] from the output
         */
        override fun toString(): String {
            return "AuthenticationRequest(username='$username', password='REDACTED', csrfToken='$csrfToken')"
        }

        /**
         * Generated because of how arrays and the auto generated equals/hashCode methods of data classes work.
         * https://rules.sonarsource.com/kotlin/RSPEC-6218
         */
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Valid

            if (username != other.username) return false
            if (!password.contentEquals(other.password)) return false
            return csrfToken == other.csrfToken
        }

        /**
         * Generated because of how arrays and the auto generated equals/hashCode methods of data classes work.
         * https://rules.sonarsource.com/kotlin/RSPEC-6218
         */
        override fun hashCode(): Int {
            var result = username.hashCode()
            result = 31 * result + password.contentHashCode()
            result = 31 * result + csrfToken.hashCode()
            return result
        }
    }
}