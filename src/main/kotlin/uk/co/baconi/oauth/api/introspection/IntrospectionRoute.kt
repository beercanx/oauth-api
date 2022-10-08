package uk.co.baconi.oauth.api.introspection

import io.ktor.server.application.*
import io.ktor.http.ContentType.Application.FormUrlEncoded
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import uk.co.baconi.oauth.api.client.ConfidentialClient
import uk.co.baconi.oauth.api.ktor.auth.authenticate
import uk.co.baconi.oauth.api.ktor.auth.extractClient

interface IntrospectionRoute {

    val introspectionService: IntrospectionService

    fun Route.introspection() {
        authenticate(ConfidentialClient::class) {
            contentType(FormUrlEncoded) {
                post<IntrospectionLocation> {
                    extractClient<ConfidentialClient> { principal ->

                        val response = when (val request = validateIntrospectionRequest(principal)) {
                            is IntrospectionRequest.Invalid -> request.toResponse()
                            is IntrospectionRequest.Valid -> introspectionService.introspect(request)
                            is IntrospectionRequest.ValidWithHint -> introspectionService.introspect(request)
                        }

                        when (response) {
                            is InvalidIntrospectionResponse -> call.respond(BadRequest, response)
                            else -> call.respond(response)
                        }
                    }
                }
            }
        }
    }
}