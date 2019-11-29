package com.sbgcore.oauth.api.wellknown

import kotlinx.serialization.Serializable

@Serializable
data class OpenIdConfiguration(
    val issuer: String,
    val authorization_endpoint: String
) // TODO - Flesh out and use more types