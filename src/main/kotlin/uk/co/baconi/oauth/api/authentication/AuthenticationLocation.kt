package uk.co.baconi.oauth.api.authentication

import io.ktor.server.locations.*

@Location("/authentication")
data class AuthenticationLocation(val redirectUri: String? = null)