package com.sbgcore.oauth.api.openid

import com.sbgcore.oauth.api.client.ClientPrincipal

import kotlin.reflect.KProperty1

/**
 * To be used when validating the current client principal
 */
fun <A : ClientPrincipal> validClientPrincipal(principal: A?): A {
    return principal ?: throw Exception("Invalid client")
}

/**
 * To be used when validating a raw requests parameter.
 * TODO - Convert into a checkNotBlank following the Kotlin standard checkNotNull method.
 */
fun <A> A.validateStringParameter(parameter: KProperty1<A, String?>): String {
    // TODO - Verify this is all we need to do
    return when (val value = parameter.get(this)?.trim()) {
        null, "" -> throw Exception("Null or blank parameter: ${parameter.name}")
        else -> value
    }
}
