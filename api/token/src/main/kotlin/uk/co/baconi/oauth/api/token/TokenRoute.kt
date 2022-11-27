package uk.co.baconi.oauth.api.token

import io.ktor.http.ContentType.Application.FormUrlEncoded
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.UnsupportedMediaType
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import uk.co.baconi.oauth.api.common.client.ClientPrincipal
import uk.co.baconi.oauth.api.common.client.ConfidentialClient
import uk.co.baconi.oauth.api.common.client.PublicClient
import uk.co.baconi.oauth.api.common.ktor.auth.authenticate
import uk.co.baconi.oauth.api.common.ktor.auth.extractClient

interface TokenRoute : TokenRequestValidation {

    val assertionGrant: AssertionGrant
    val authorisationCodeGrant: AuthorisationCodeGrant
    val passwordGrant: PasswordGrant
    val refreshTokenGrant: RefreshTokenGrant

    fun Route.token() {

        application.log.info("Registering the TokenRoute.token() routes")

        route("/token") {
            authenticate(ConfidentialClient::class, PublicClient::class) {
                contentType(FormUrlEncoded) {
                    post {

                        val principal = call.extractClient<ClientPrincipal>()

                        val response = when (val request = call.validateTokenRequest(principal)) {
                            is TokenRequest.Invalid -> request.toResponse()
                            is PasswordRequest -> passwordGrant.exchange(request)
                            is AuthorisationCodeRequest -> authorisationCodeGrant.exchange(request)
                            is RefreshTokenRequest -> refreshTokenGrant.exchange(request)
                            is AssertionRequest -> assertionGrant.exchange(request)
                        }

                        when (response) {
                            is TokenResponse.Failed -> call.respond(BadRequest, response)
                            is TokenResponse.Success -> call.respond(response)
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