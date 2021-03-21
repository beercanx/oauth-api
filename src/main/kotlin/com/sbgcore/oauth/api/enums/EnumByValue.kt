package com.sbgcore.oauth.api.enums

/**
 * Find an [Enum] with a [WithValue] by its value, instead of name or index.
 */
inline fun <reified A> enumByValue(value: String): A? where A : Enum<A>, A : WithValue {
    return enumValues<A>().find { a -> a.value == value }
}