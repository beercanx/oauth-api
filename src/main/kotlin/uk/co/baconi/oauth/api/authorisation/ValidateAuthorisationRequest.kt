package uk.co.baconi.oauth.api.authorisation

import uk.co.baconi.oauth.api.client.ClientConfigurationRepository
import uk.co.baconi.oauth.api.client.ClientId
import uk.co.baconi.oauth.api.enums.deserialise
import uk.co.baconi.oauth.api.ktor.isAbsoluteURI
import uk.co.baconi.oauth.api.scopes.Scopes

/**
 * Validate request based on https://tools.ietf.org/html/rfc6749#section-4.1.1,
 * mixed with some custom logic to support gaining an authentication decision.
 */
fun validateAuthorisationRequest(
    location: AuthorisationLocation,
    clientConfigurationRepository: ClientConfigurationRepository
): AuthorisationRequest {

    val clientId = location.client_id?.let { s -> deserialise<ClientId>(s) }
    val clientConfiguration = clientId?.let(clientConfigurationRepository::findByClientId)

    val redirectUri = clientConfiguration?.redirectUrls?.find { r -> r == location.redirect_uri }

    val responseType = location.response_type?.let { s -> deserialise<AuthorisationResponseType>(s) }
    val validResponseType = clientConfiguration?.allowedResponseTypes?.find { r -> r == responseType }

    val rawScopes = location.scope?.split(" ")
    val scopes = rawScopes?.mapNotNull { s -> deserialise<Scopes>(s) }
    val validScopes = clientConfiguration?.allowedScopes?.let { allowedScopes ->
        scopes?.filter { s -> allowedScopes.contains(s) }?.toSet()
    }

    // TODO - Make sure we validate enough to respond https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.2.1
    return when {

        location.client_id == null -> AuthorisationRequest.Invalid//(invalid_request, "missing parameter: redirect_uri")
        clientId == null -> AuthorisationRequest.Invalid//(unauthorized_client, "unauthorized client")
        clientConfiguration == null -> AuthorisationRequest.Invalid//(unauthorized_client, "unauthorized client")

        // TODO - How do we redirect back to the consumer with no valid client_id?

        location.redirect_uri == null -> AuthorisationRequest.Invalid//(invalid_request, "missing parameter: redirect_uri")
        redirectUri == null -> AuthorisationRequest.Invalid//(invalid_request, "invalid parameter: redirect_uri")
        !redirectUri.isAbsoluteURI(false) -> AuthorisationRequest.Invalid//(invalid_request, "invalid parameter: redirect_uri")

        // TODO - How do we redirect back to the consumer with no valid redirect_uri?

        // Support aborting an authorisation via a users choice
        location.abort == true -> AuthorisationRequest.Aborted(location.redirect_uri)

        location.response_type == null -> AuthorisationRequest.Invalid//(invalid_request, "missing parameter: response_type")
        responseType == null -> AuthorisationRequest.Invalid//(unsupported_response_type, "unsupported response type: ${location.response_type}")
        validResponseType == null -> AuthorisationRequest.Invalid//(unauthorized_client, "unauthorized client")

        location.state.isNullOrBlank() -> AuthorisationRequest.Invalid//(invalid_request, "missing parameter: state")

        // The requested scope is invalid, unknown, or malformed.
        rawScopes?.size != scopes?.size -> AuthorisationRequest.Invalid//(invalid_request, "invalid parameter: scope")

        else -> AuthorisationRequest.Valid(
            responseType = responseType,
            clientId = clientId,
            redirectUri = redirectUri,
            state = location.state,
            requestedScope = validScopes ?: emptySet()
        )
    }
}