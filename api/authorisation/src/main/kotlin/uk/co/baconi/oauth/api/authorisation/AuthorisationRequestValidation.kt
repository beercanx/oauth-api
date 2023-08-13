package uk.co.baconi.oauth.api.authorisation

import io.ktor.http.*
import io.ktor.server.application.*
import uk.co.baconi.oauth.api.common.authorisation.AuthorisationResponseType
import uk.co.baconi.oauth.api.common.authorisation.AuthorisationResponseType.Code
import uk.co.baconi.oauth.api.common.client.*
import uk.co.baconi.oauth.api.common.client.ClientAction.Authorise
import uk.co.baconi.oauth.api.common.grant.GrantType
import uk.co.baconi.oauth.api.common.grant.GrantType.AuthorisationCode
import uk.co.baconi.oauth.api.common.scope.ScopesSerializer

private const val CLIENT_ID = "client_id"
private const val REDIRECT_ID = "redirect_uri"
private const val RESPONSE_TYPE = "response_type"
private const val SCOPE = "scope"
private const val STATE = "state"
private const val ABORT = "abort"

interface AuthorisationRequestValidation {

    val clientConfigurationRepository: ClientConfigurationRepository

    suspend fun ApplicationCall.validateAuthorisationRequest(): AuthorisationRequest {
        return validateAuthorisationRequest(request.queryParameters)
    }

    fun validateAuthorisationRequest(params: Parameters): AuthorisationRequest {

        val config = params[CLIENT_ID]?.let(clientConfigurationRepository::findByClientId)
        val principal = config?.let(ClientPrincipal::fromConfiguration)

        val redirectUri = params[REDIRECT_ID]
        val responseType = params[RESPONSE_TYPE]?.let(AuthorisationResponseType::fromValueOrNull)
        val scopes = params[SCOPE]?.let(ScopesSerializer::deserialize) ?: emptySet()
        val state = params[STATE]
        val abort = params[ABORT]?.toBooleanStrictOrNull() ?: false

        return when {

            config == null -> AuthorisationRequest.InvalidClient("missing")
            principal == null -> AuthorisationRequest.InvalidClient("missing")

            redirectUri == null -> AuthorisationRequest.InvalidRedirect("missing")
            !principal.hasRedirectUri(redirectUri) -> AuthorisationRequest.InvalidRedirect("invalid")
            !redirectUri.isAbsoluteURI(false) -> AuthorisationRequest.InvalidRedirect("invalid")

            // Currently only support authorisation code
            !(principal.can(Authorise) && principal.can(AuthorisationCode)) -> AuthorisationRequest.Invalid(
                redirectUri = redirectUri,
                error = "unauthorized_client",
                description = "unauthorized client",
                state = state
            )

            params[RESPONSE_TYPE] == null -> AuthorisationRequest.Invalid(
                redirectUri = redirectUri,
                error = "invalid_request",
                description = "missing parameter: response_type",
                state = state
            )
            responseType == null || responseType != Code -> AuthorisationRequest.Invalid(
                redirectUri = redirectUri,
                error = "unsupported_response_type",
                description = "unsupported response type: ${params[RESPONSE_TYPE]}",
                state = state
            )

            // Enforce the use of a state parameter
            state == null -> AuthorisationRequest.Invalid(
                redirectUri = redirectUri,
                error = "invalid_request",
                description = "missing parameter: state",
                state = null
            )
            state.isBlank() -> AuthorisationRequest.Invalid(
                redirectUri = redirectUri,
                error = "invalid_request",
                description = "invalid parameter: state",
                state = state
            )

            // The requested scope is invalid, unknown, or malformed.
            !scopes.all(principal::canBeIssued) -> AuthorisationRequest.Invalid(
                redirectUri = redirectUri,
                error = "invalid_request",
                description = "invalid parameter: scope",
                state = state
            )

            // Support aborting an authorisation via a users choice
            abort -> AuthorisationRequest.Invalid(
                redirectUri = redirectUri,
                error = "access_denied",
                description = "user aborted",
                state = state
            )

            else -> AuthorisationRequest.Valid(
                responseType = responseType,
                clientId = config.id,
                redirectUri = redirectUri,
                state = state,
                scopes = scopes
            )
        }
    }
}
