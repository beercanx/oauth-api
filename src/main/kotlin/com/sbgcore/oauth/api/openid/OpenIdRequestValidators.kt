package com.sbgcore.oauth.api.openid

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import arrow.core.toOption
import com.sbgcore.oauth.api.authentication.ClientPrincipal

fun <A : ClientPrincipal> validClientPrincipal(principal: A?): Either<Throwable, A> {
    return principal.toOption().toEither<Throwable> {
        Exception("Invalid client")
    }
}

fun validParameter(name: String, value: String?): Either<Throwable, String> {
    // TODO - Verify this is all we need to do
    return if (!value.isNullOrBlank()) {
        value.right()
    } else {
        Exception("Null or blank parameter: $name").left()
    }
}