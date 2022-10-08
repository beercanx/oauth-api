package uk.co.baconi.oauth.api.ktor

import java.net.URI

/**
 * Does this [String] contain an absolute [URI].
 * @param default to return when we cannot parse the [String] as a [URI].
 */
fun String.isAbsoluteURI(default: Boolean = true): Boolean = try {
    URI.create(this).isAbsolute
} catch (exception: Exception) {
    default
}
