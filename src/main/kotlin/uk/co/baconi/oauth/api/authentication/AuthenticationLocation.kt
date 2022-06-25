package uk.co.baconi.oauth.api.authentication

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("/authentication")
data class AuthenticationLocation(val redirectUri: String? = null)