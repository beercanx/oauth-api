package uk.co.baconi.oauth.api.wellknown

import io.ktor.server.locations.*

@Location("/.well-known/jwks.json")
object JsonWebKeySetLocation