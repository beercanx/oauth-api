package uk.co.baconi.oauth.api.exchange

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.auth.HttpAuthHeader.Companion.basicAuthChallenge
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import uk.co.baconi.oauth.api.OAuth2Server.REALM
import uk.co.baconi.oauth.api.client.ClientAuthenticationService
import uk.co.baconi.oauth.api.client.ConfidentialClient
import uk.co.baconi.oauth.api.client.PublicClient
import uk.co.baconi.oauth.api.exchange.ErrorType.InvalidRequest
import uk.co.baconi.oauth.api.exchange.grants.assertion.AssertionRedemptionGrant
import uk.co.baconi.oauth.api.exchange.grants.authorisation.AuthorisationCodeGrant
import uk.co.baconi.oauth.api.exchange.grants.password.PasswordCredentialsGrant
import uk.co.baconi.oauth.api.exchange.grants.refresh.RefreshGrant
import uk.co.baconi.oauth.api.ktor.auth.authenticate
import kotlin.text.Charsets.UTF_8

interface ExchangeRoute {

    val passwordCredentialsGrant: PasswordCredentialsGrant
    val refreshGrant: RefreshGrant
    val authorisationCodeGrant: AuthorisationCodeGrant
    val assertionRedemptionGrant: AssertionRedemptionGrant
    val clientAuthService: ClientAuthenticationService

    fun Route.exchange() {

        // Optional because we have to cater for public clients using PKCE
        authenticate(ConfidentialClient::class, PublicClient::class, optional = true) {
            post<ExchangeLocation> {

                // Handle standard exchanges for confidential clients
                when (val client = call.principal<ConfidentialClient>()) {
                    is ConfidentialClient -> {

                        val parameters = call.receive<Parameters>()

                        val response = when (val request = validateExchangeRequest(client, parameters)) {
                            is AuthorisationCodeRequest -> authorisationCodeGrant.exchange(request)
                            is PasswordRequest -> passwordCredentialsGrant.exchange(request)
                            is RefreshTokenRequest -> refreshGrant.exchange(request)
                            is AssertionRequest -> assertionRedemptionGrant.exchange(request)
                            is InvalidConfidentialExchangeRequest -> FailedExchangeResponse(InvalidRequest) // TODO - Extend to include more detail?
                        }

                        return@post when(response) {
                            is SuccessExchangeResponse -> call.respond(response)
                            is FailedExchangeResponse ->  call.respond(BadRequest, response) // TODO - Review if the spec allows for any other type of response codes, maybe around invalid_client responses.
                        }
                    }
                }

                // Handle PKCE requests for public clients
                when (val client = call.principal<PublicClient>()) {
                    is PublicClient -> {

                        val parameters = call.receive<Parameters>()

                        val response = when (val result = validatePkceExchangeRequest(client, parameters)) {
                            is PkceAuthorisationCodeRequest -> authorisationCodeGrant.exchange(result)
                            is InvalidPublicExchangeRequest -> FailedExchangeResponse(InvalidRequest) // TODO - Extend to include more detail?
                        }

                        return@post when(response) {
                            is SuccessExchangeResponse -> call.respond(response)
                            is FailedExchangeResponse ->  call.respond(BadRequest, response) // TODO - Review if the spec allows for any other type of response codes, maybe around invalid_client responses.
                        }
                    }
                }

                // 401 - Invalid credentials?
                // TODO - Review this "default" error response
                // TODO - Setup a common response provider
                return@post call.respond(UnauthorizedResponse(basicAuthChallenge(REALM, UTF_8)))
            }
        }
    }
}
