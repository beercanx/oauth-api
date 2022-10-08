package uk.co.baconi.oauth.api.common.authentication

import kotlinx.serialization.Serializable

@Serializable(with = AuthenticatedUsernameSerializer::class)
data class AuthenticatedUsername(val value: String)
