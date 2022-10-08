package uk.co.baconi.oauth.api.introspection

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("/introspect")
class IntrospectionLocation