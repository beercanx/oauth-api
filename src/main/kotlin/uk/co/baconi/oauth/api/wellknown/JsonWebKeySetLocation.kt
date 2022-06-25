package uk.co.baconi.oauth.api.wellknown

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("/.well-known/jwks.json")
class JsonWebKeySetLocation