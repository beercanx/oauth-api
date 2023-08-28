package uk.co.baconi.oauth.automation.api

import com.typesafe.config.Config
import com.typesafe.config.ConfigObject
import java.net.URI

fun Config.getUri(path: String): URI = getString(path).let(URI::create)

fun Config.getUriOrNull(path: String): URI? = if(hasPath(path)) getUri(path) else null

fun Config.getStringOrNull(path: String): String? = if(hasPath(path)) getString(path) else null

inline fun <reified E : Enum<E>> Config.getEnumSetOrEmpty(path: String): Set<E> = if(hasPath(path)) {
    getEnumList(E::class.java, path).toSet()
} else {
    emptySet()
}

fun ConfigObject.getConfig(path: String): Config = toConfig().getConfig(path)