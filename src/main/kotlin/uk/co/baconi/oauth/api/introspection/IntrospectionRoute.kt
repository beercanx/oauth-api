package uk.co.baconi.oauth.api.introspection

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import uk.co.baconi.oauth.api.client.ConfidentialClient
import uk.co.baconi.oauth.api.ktor.auth.authenticate
import uk.co.baconi.oauth.api.ktor.auth.extractClient

interface IntrospectionRoute {

    val introspectionService: IntrospectionService

    fun Route.introspection() {
        authenticate(ConfidentialClient::class) {
            post<IntrospectionLocation> {
                extractClient<ConfidentialClient> { principal ->

                    val response = when (val request = validateIntrospectionRequest(principal)) {
                        is IntrospectionRequest.Invalid -> request.toResponse()
                        is IntrospectionRequest.Valid -> introspectionService.introspect(request)
                        is IntrospectionRequest.ValidWithHint -> introspectionService.introspect(request)
                    }

                    when(response) {
                        is InvalidIntrospectionResponse -> call.respond(BadRequest, response)
                        else -> call.respond(response)
                    }
                }
            }
        }
    }
}