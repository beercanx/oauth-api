package uk.co.baconi.oauth.api.introspection

import uk.co.baconi.oauth.api.client.ConfidentialClient
import uk.co.baconi.oauth.api.ktor.auth.authenticate
import uk.co.baconi.oauth.api.ktor.auth.extractClient
import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.routing.post

interface IntrospectionRoute {

    val introspectionService: IntrospectionService

    fun Route.introspection() {
        authenticate(ConfidentialClient::class) {
            post<IntrospectionLocation> {
                extractClient<ConfidentialClient> { principal ->

                    val response = when (val request = validateIntrospectionRequest(principal)) {
                        is IntrospectionRequest -> introspectionService.introspect(request)
                        is IntrospectionRequestWithHint -> introspectionService.introspect(request)
                    }

                    call.respond(response)
                }
            }
        }
    }
}