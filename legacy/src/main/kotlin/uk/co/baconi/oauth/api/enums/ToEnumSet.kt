package uk.co.baconi.oauth.api.enums

inline fun <reified T : Enum<T>> List<String>?.toEnumSet(): Set<T> {
    return this?.mapNotNull { e -> e.deserialise<T>() }?.toSet() ?: emptySet()
}
