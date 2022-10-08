package uk.co.baconi.oauth.api.authentication

import java.util.*

data class AuthenticationSession(val csrfToken: String) {
    constructor(csrfToken: UUID) : this(csrfToken.toString())
}
