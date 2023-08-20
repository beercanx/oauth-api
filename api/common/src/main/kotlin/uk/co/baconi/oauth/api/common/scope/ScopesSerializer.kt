package uk.co.baconi.oauth.api.common.scope

import kotlinx.serialization.SerializationException

/**
 * Custom scope field serializer because OAuth spec requires it to be a space separated string field.
 * For example, it will look like: `basic profile::read`
 */
object ScopesSerializer : SpaceDelimitedSerializer<Scope>() {
    override fun encode(value: Scope): String = value.value
    override fun decode(string: String): Scope = throw SerializationException("Decoding not supported")
}