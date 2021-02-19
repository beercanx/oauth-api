package com.sbgcore.oauth.api.openid

import arrow.fx.IO
import arrow.fx.extensions.io.applicative.just
import arrow.fx.extensions.io.applicativeError.raiseError
import com.sbgcore.oauth.api.authentication.ClientPrincipal
import kotlin.reflect.KProperty1

/**
 * To be used when validating the current client principal
 */
fun <A : ClientPrincipal> validClientPrincipal(principal: A?): IO<A> {
    return principal?.just() ?: Exception("Invalid client").raiseError()
}

/**
 * To be used when validating a raw requests parameter.
 */
fun <T> T.validateStringParameter(parameter: KProperty1<T, String?>): IO<String> {
    // TODO - Verify this is all we need to do
    return when(val value = parameter.get(this)?.trim()) {
        null, "" -> Exception("Null or blank parameter: ${parameter.name}").raiseError()
        else -> value.just()
    }
}