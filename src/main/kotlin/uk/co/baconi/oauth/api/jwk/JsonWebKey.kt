package uk.co.baconi.oauth.api.jwk

import kotlinx.serialization.Serializable

@Serializable
data class JsonWebKey(
    val kty: String,
    val use: String,
    val alg: String
) // TODO - Flesh out and give proper names with mappings