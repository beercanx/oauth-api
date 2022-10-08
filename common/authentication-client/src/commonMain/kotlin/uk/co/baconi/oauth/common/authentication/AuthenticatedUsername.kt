package uk.co.baconi.oauth.common.authentication

import kotlinx.serialization.Serializable

@Serializable(with = AuthenticatedUsernameSerializer::class)
data class AuthenticatedUsername(val value: String)
