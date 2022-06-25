package uk.co.baconi.oauth.api.exchange

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.http.ContentType.Application.FormUrlEncoded
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.server.locations.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import uk.co.baconi.oauth.api.authorisation.AuthorisationCodeService
import uk.co.baconi.oauth.api.client.ClientAuthenticationService
import uk.co.baconi.oauth.api.client.ClientPrincipal
import uk.co.baconi.oauth.api.client.ConfidentialClient
import uk.co.baconi.oauth.api.client.PublicClient
import uk.co.baconi.oauth.api.exchange.grants.assertion.AssertionRedemptionGrant
import uk.co.baconi.oauth.api.exchange.grants.authorisation.AuthorisationCodeGrant
import uk.co.baconi.oauth.api.exchange.grants.password.PasswordCredentialsGrant
import uk.co.baconi.oauth.api.exchange.grants.refresh.RefreshGrant
import uk.co.baconi.oauth.api.ktor.auth.authenticate

interface ExchangeRoute {

    val passwordCredentialsGrant: PasswordCredentialsGrant
    val refreshGrant: RefreshGrant
    val authorisationCodeGrant: AuthorisationCodeGrant
    val assertionRedemptionGrant: AssertionRedemptionGrant

    val authorisationCodeService: AuthorisationCodeService
    val clientAuthService: ClientAuthenticationService

    fun Route.exchange() {
        authenticate(ConfidentialClient::class, PublicClient::class) {
            contentType(FormUrlEncoded) {
                post<ExchangeLocation> {

                    // Get either the confidential client or the public client authentication.
                    when (val client = call.principal<ClientPrincipal>()) {

                        // This shouldn't be possible, as the `authenticate` should have returned a 401
                        null -> {
                            application.log.error("Exchange request's principal was null, there must be a coding mistake somewhere.")
                            call.respond(InternalServerError)
                        }

                        // Handle standard exchanges for confidential clients
                        is ConfidentialClient -> {

                            val response =
                                when (val request = validateExchangeRequest(authorisationCodeService, client)) {
                                    is InvalidConfidentialExchangeRequest -> FailedExchangeResponse(
                                        request.error,
                                        request.description
                                    )
                                    is AuthorisationCodeRequest -> authorisationCodeGrant.exchange(request)
                                    is PasswordRequest -> passwordCredentialsGrant.exchange(request)
                                    is RefreshTokenRequest -> refreshGrant.exchange(request)
                                    is AssertionRequest -> assertionRedemptionGrant.exchange(request)
                                }

                            when (response) {
                                is FailedExchangeResponse -> call.respond(BadRequest, response)
                                is SuccessExchangeResponse -> call.respond(response)
                            }
                        }

                        // Handle PKCE requests for public clients
                        is PublicClient -> {

                            val response =
                                when (val request = validatePkceExchangeRequest(authorisationCodeService, client)) {
                                    is InvalidPublicExchangeRequest -> FailedExchangeResponse(
                                        request.error,
                                        request.description
                                    )
                                    is PkceAuthorisationCodeRequest -> authorisationCodeGrant.exchange(request)
                                }

                            when (response) {
                                is FailedExchangeResponse -> call.respond(BadRequest, response)
                                is SuccessExchangeResponse -> call.respond(response)
                            }
                        }
                    }
                }
            }
        }
    }
}
