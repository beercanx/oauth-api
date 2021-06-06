package uk.co.baconi.oauth.api.authorisation

import uk.co.baconi.oauth.api.client.ClientConfigurationRepository
import uk.co.baconi.oauth.api.client.ClientId
import uk.co.baconi.oauth.api.enums.deserialise
import uk.co.baconi.oauth.api.ktor.isAbsoluteURI
import uk.co.baconi.oauth.api.scopes.parseAsScopes

/**
 * Validate request based on https://tools.ietf.org/html/rfc6749#section-4.1.1,
 * mixed with some custom logic to support gaining an authentication decision.
 */
fun validateAuthorisationRequest(
    location: AuthorisationLocation,
    clientConfigurationRepository: ClientConfigurationRepository
): AuthorisationRequest {

    val validClientId = location.client_id?.deserialise<ClientId>()
    val validClientConfiguration = validClientId?.let(clientConfigurationRepository::findByClientId)

    // Check if the direct uri is defined in the clients configuration
    val validRedirectUri = validClientConfiguration?.redirectUris?.find { r -> r == location.redirect_uri }

    // Check if the response type is a supported response type
    val responseType = location.response_type?.deserialise<AuthorisationResponseType>()

    // Check if the response type is allowed for the client
    val validResponseType = validClientConfiguration?.allowedResponseTypes?.find { r -> r == responseType }

    // Parse the scopes into the individual stages.
    val (rawScopes, parsedScopes, validScopes) = location.scope.parseAsScopes(validClientConfiguration)

    // TODO - Make sure we validate enough to respond https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.2.1
    return when {

        location.client_id == null -> AuthorisationRequest.Invalid("invalid_request", "missing parameter: redirect_uri")
        validClientId == null -> AuthorisationRequest.Invalid("unauthorized_client", "unauthorized client")
        validClientConfiguration == null -> AuthorisationRequest.Invalid("unauthorized_client", "unauthorized client")

        // TODO - How do we redirect back to the consumer with no valid client_id?

        location.redirect_uri == null -> AuthorisationRequest.Invalid("invalid_request", "missing parameter: redirect_uri")
        validRedirectUri == null -> AuthorisationRequest.Invalid("invalid_request", "invalid parameter: redirect_uri")
        !validRedirectUri.isAbsoluteURI(false) -> AuthorisationRequest.Invalid("invalid_request", "invalid parameter: redirect_uri")

        // TODO - How do we redirect back to the consumer with no valid redirect_uri?

        // Support aborting an authorisation via a users choice
        location.abort == true -> AuthorisationRequest.Aborted(location.redirect_uri)

        location.response_type == null -> AuthorisationRequest.Invalid("invalid_request", "missing parameter: response_type")
        responseType == null -> AuthorisationRequest.Invalid("unsupported_response_type", "unsupported response type: ${location.response_type}")
        validResponseType == null -> AuthorisationRequest.Invalid("unauthorized_client", "unauthorized client")

        // Enforce the use of a state parameter
        location.state.isNullOrBlank() -> AuthorisationRequest.Invalid("invalid_request", "missing parameter: state")

        // The requested scope is invalid, unknown, or malformed.
        rawScopes?.size != parsedScopes?.size -> AuthorisationRequest.Invalid("invalid_request", "invalid parameter: scope")

        // TODO - Do we reject if the scope parsed size is different from the valid size?

        else -> AuthorisationRequest.Valid(
            responseType = validResponseType,
            clientId = validClientId,
            redirectUri = validRedirectUri,
            state = location.state,
            requestedScope = validScopes
        )
    }
}