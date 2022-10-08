package uk.co.baconi.oauth.api.authentication

import java.util.*

// TODO - Consider does this session name make sense, its intended purpose is a "before" authenticated session
data class AuthenticationSession(val csrfToken: String) {
    constructor(csrfToken: UUID) : this(csrfToken.toString())
}
