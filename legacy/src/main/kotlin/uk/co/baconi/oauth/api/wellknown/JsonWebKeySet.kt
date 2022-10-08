package uk.co.baconi.oauth.api.wellknown

import kotlinx.serialization.Serializable

@Serializable
data class JsonWebKeySet(val keys: Set<JsonWebKey>)