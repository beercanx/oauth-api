package com.sbgcore.oauth.api.jwk

import kotlinx.serialization.Serializable

@Serializable
class JsonWebKeySet(val keys: Set<JsonWebKey>)