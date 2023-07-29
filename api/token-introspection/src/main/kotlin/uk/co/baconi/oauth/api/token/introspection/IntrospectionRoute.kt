package uk.co.baconi.oauth.api.token.introspection

import io.ktor.http.ContentType.Application.FormUrlEncoded
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Forbidden
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.http.HttpStatusCode.Companion.UnsupportedMediaType
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import uk.co.baconi.oauth.api.common.client.ConfidentialClient
import uk.co.baconi.oauth.api.common.ktor.auth.authenticate
import uk.co.baconi.oauth.api.common.ktor.auth.extractClient
import uk.co.baconi.oauth.api.token.introspection.IntrospectionErrorType.InvalidRequest
import uk.co.baconi.oauth.api.token.introspection.IntrospectionErrorType.UnauthorizedClient
import uk.co.baconi.oauth.api.token.introspection.IntrospectionRequestValidation.validateIntrospectionRequest

interface IntrospectionRoute {

    val introspectionService: IntrospectionService

    fun Route.introspection() {

        application.log.info("Registering the IntrospectionRoute.introspection() routes")

        route("/introspect") {
            authenticate(ConfidentialClient::class) {
                contentType(FormUrlEncoded) {
                    post {

                        val principal = call.extractClient<ConfidentialClient>()

                        val response = when (val request = call.validateIntrospectionRequest(principal)) {
                            is IntrospectionRequest.Invalid -> request.toResponse()
                            is IntrospectionRequest.Valid -> introspectionService.introspect(request)
                        }

                        when (response) {
                            is IntrospectionResponse.Invalid -> when(response.error) {
                                InvalidRequest -> call.respond(BadRequest, response)
                                UnauthorizedClient -> call.respond(Forbidden, response)
                            }
                            is IntrospectionResponse.Inactive -> call.respond(OK, response)
                            is IntrospectionResponse.Active -> call.respond(OK, response)
                        }
                    }
                }
                post {
                    call.response.status(UnsupportedMediaType)
                }
            }
        }
    }
}