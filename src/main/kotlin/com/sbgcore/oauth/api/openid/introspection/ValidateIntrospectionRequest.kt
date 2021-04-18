package com.sbgcore.oauth.api.openid.introspection

import com.sbgcore.oauth.api.checkNotBlank
import com.sbgcore.oauth.api.client.ConfidentialClient
import com.sbgcore.oauth.api.ktor.ApplicationContext
import io.ktor.application.*
import io.ktor.request.*

suspend fun ApplicationContext.validateIntrospectionRequest(
    principal: ConfidentialClient
): ValidatedIntrospectionRequest {

    val raw = call.receive<RawIntrospectionRequest>()

    val token = checkNotBlank(raw.token) { "token" }
    val hint = raw.hint

    return if (hint == null) {
        IntrospectionRequest(principal, token)
    } else {
        IntrospectionRequestWithHint(principal, token, hint)
    }
}
