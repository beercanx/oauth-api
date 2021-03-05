package com.sbgcore.oauth.api.openid

import arrow.core.*
import arrow.fx.IO
import arrow.fx.extensions.io.applicative.just
import arrow.fx.extensions.io.applicativeError.raiseError
import com.sbgcore.oauth.api.authentication.ClientPrincipal
import com.sbgcore.oauth.api.authentication.PublicClient
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.util.pipeline.*
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
fun <A> A.validateStringParameter(parameter: KProperty1<A, String?>): IO<String> {
    // TODO - Verify this is all we need to do
    return when(val value = parameter.get(this)?.trim()) {
        null, "" -> Exception("Null or blank parameter: ${parameter.name}").raiseError()
        else -> value.just()
    }
}

/**
 * Extract the client_id from the body, check that its a valid PKCE client and then save it as the current Principal.
 *
 * TODO - Think about placing somewhere else?
 */
fun PipelineContext<*, ApplicationCall>.validPkceClient(parameters: Parameters): Option<PublicClient> {
    return parameters["client_id"].toOption().flatMap(::validatePkceClient).also { client ->
        if (client is Some<PublicClient>) call.authentication.principal(client.t)
    }
}

/**
 * TODO - Lookup against client config / database
 */
fun validatePkceClient(clientId: String): Option<PublicClient> {

    val client: ClientId? = try {
        enumValueOf<ClientId>(clientId)
    } catch (exception: Exception) {
        null
    }

    return if(client == null) {
        none()
    } else {
        PublicClient(client).some()
    }
}