package uk.co.baconi.oauth.api.common.scope

/**
 * Custom scope field serializer because OAuth spec requires it to be a space separated string field.
 * For example, it will look like: `openid profile::read`
 */
object ScopesSerializer : SpaceDelimitedSerializer<Scope>() {
    override fun encode(value: Scope): String = value.value
    override fun decode(string: String): Scope? = Scope.fromValueOrNull(string)
}