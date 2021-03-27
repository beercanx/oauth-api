package com.sbgcore.oauth.api.openid

import com.sbgcore.oauth.api.authentication.ClientPrincipal
import com.sbgcore.oauth.api.authentication.PublicClient
import com.sbgcore.oauth.api.enums.enumByValue

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.util.pipeline.*
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
    return when(val value = parameter.get(this)?.trim()) {
        null, "" -> throw Exception("Null or blank parameter: ${parameter.name}")
        else -> value
    }
}

/**
 * Extract the client_id from the body, check that its a valid Public Client and then save it as the current Principal.
 */
fun PipelineContext<*, ApplicationCall>.validPublicClient(parameters: Parameters): PublicClient? {
    return parameters["client_id"]?.let(::validatePublicClient)?.also { client ->
        call.authentication.principal(client)
    }
}

private fun validatePublicClient(clientId: String): PublicClient? {
    // TODO - Check if client is public before wrapping in PublicClient
    return enumByValue<ClientId>(clientId)?.let(::PublicClient)
}