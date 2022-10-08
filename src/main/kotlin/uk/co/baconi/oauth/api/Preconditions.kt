package uk.co.baconi.oauth.api

/**
 * Throws an [IllegalStateException] with the result of calling [lazyMessage] and a descriptor if
 * the [value] is null or blank. Otherwise returns the not null value.
 */
inline fun checkNotBlank(value: String?, lazyMessage: () -> Any): String {

    checkNotNull(value) {
        "${lazyMessage()} was null"
    }

    if (value.isBlank()) {
        throw IllegalStateException("${lazyMessage()} was blank")
    } else {
        return value
    }
}