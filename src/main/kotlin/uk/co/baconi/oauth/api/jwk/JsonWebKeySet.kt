package uk.co.baconi.oauth.api.jwk

import kotlinx.serialization.Serializable

@Serializable
data class JsonWebKeySet(val keys: Set<JsonWebKey>)