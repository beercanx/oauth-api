package com.sbgcore.oauth.api.wellknown

import kotlinx.serialization.Serializable

@Serializable
class JsonWebKeySet(val keys: Set<JsonWebKey>)