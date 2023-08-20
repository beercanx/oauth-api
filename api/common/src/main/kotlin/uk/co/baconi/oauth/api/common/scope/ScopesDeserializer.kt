package uk.co.baconi.oauth.api.common.scope

import kotlinx.serialization.SerializationException

/**
 * Custom scope field serializer because OAuth spec requires it to be a space separated string field.
 * For example, it will look like: `basic profile::read`
 */
object ScopesDeserializer : SpaceDelimitedSerializer<String>() {
    override fun encode(value: String): String = throw SerializationException("Serialisation not supported")
    override fun decode(string: String): String? = string.ifBlank { null }
}