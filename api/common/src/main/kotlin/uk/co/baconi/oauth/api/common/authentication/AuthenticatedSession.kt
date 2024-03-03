package uk.co.baconi.oauth.api.common.authentication

import kotlinx.serialization.Serializable

@Serializable
data class AuthenticatedSession(val username: AuthenticatedUsername)