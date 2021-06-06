package uk.co.baconi.oauth.api.introspection

import io.ktor.application.*
import io.ktor.request.*
import uk.co.baconi.oauth.api.checkNotBlank
import uk.co.baconi.oauth.api.client.ConfidentialClient
import uk.co.baconi.oauth.api.ktor.ApplicationContext

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
