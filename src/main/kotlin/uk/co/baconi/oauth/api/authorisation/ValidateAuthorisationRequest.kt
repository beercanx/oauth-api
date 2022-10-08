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
    val (rawMatchedParsed, parsedMatchedValid, validScopes) = location.scope.parseAsScopes(validClientConfiguration)

    return when {

        location.client_id == null -> AuthorisationRequest.InvalidClient("missing")
        validClientId == null -> AuthorisationRequest.InvalidClient("invalid")
        validClientConfiguration == null -> AuthorisationRequest.InvalidClient("invalid")

        location.redirect_uri == null -> AuthorisationRequest.InvalidRedirect("missing")
        validRedirectUri == null -> AuthorisationRequest.InvalidRedirect("invalid")
        !validRedirectUri.isAbsoluteURI(false) -> AuthorisationRequest.InvalidRedirect("invalid")

        // Support aborting an authorisation via a users choice
        location.abort == true -> AuthorisationRequest.Invalid(
            redirectUri = location.redirect_uri,
            error = "access_denied",
            description = "user aborted",
            state = location.state
        )

        location.response_type == null -> AuthorisationRequest.Invalid(
            redirectUri = validRedirectUri,
            error = "invalid_request",
            description = "missing parameter: response_type",
            state = location.state
        )
        responseType == null -> AuthorisationRequest.Invalid(
            redirectUri = validRedirectUri,
            error = "unsupported_response_type",
            description = "unsupported response type: ${location.response_type}",
            state = location.state
        )
        validResponseType == null -> AuthorisationRequest.Invalid(
            redirectUri = validRedirectUri,
            error = "unauthorized_client",
            description = "unauthorized client",
            state = location.state
        )

        // Enforce the use of a state parameter
        location.state.isNullOrBlank() -> AuthorisationRequest.Invalid(
            redirectUri = validRedirectUri,
            error = "invalid_request",
            description = "missing parameter: state",
            state = location.state
        )

        location.scope == null -> AuthorisationRequest.Invalid(
            redirectUri = validRedirectUri,
            error = "invalid_request",
            description = "missing parameter: scope",
            state = location.state
        )
        // The requested scope is invalid, unknown, or malformed.
        !rawMatchedParsed -> AuthorisationRequest.Invalid(
            redirectUri = validRedirectUri,
            error = "invalid_request",
            description = "invalid parameter: scope",
            state = location.state
        )
        // TODO - Do we reject if the scope parsed size is different from the valid size?
        !parsedMatchedValid -> AuthorisationRequest.Invalid(
            redirectUri = validRedirectUri,
            error = "invalid_request",
            description = "invalid parameter: scope",
            state = location.state
        )

        else -> AuthorisationRequest.Valid(
            responseType = validResponseType,
            clientId = validClientId,
            redirectUri = validRedirectUri,
            state = location.state,
            requestedScope = validScopes
        )
    }
}