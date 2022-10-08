package uk.co.baconi.oauth.api.authorisation

import uk.co.baconi.oauth.api.client.ClientId
import uk.co.baconi.oauth.api.enums.deserialise
import uk.co.baconi.oauth.api.scopes.Scopes

/**
 * Validate request based on https://tools.ietf.org/html/rfc6749#section-4.1.1,
 * mixed with some custom logic to support gaining an authentication decision.
 */
fun validateAuthorisationRequest(location: AuthorisationLocation): AuthorisationRequest {

    val responseType = location.response_type?.let { s -> deserialise<ResponseType>(s) }

    val clientId = location.client_id?.let { s -> deserialise<ClientId>(s) }

    // TODO - Make code shared with RawExchangeRequest
    // TODO - Look into reporting invalid or unknown scopes
    val scope = location.scope?.split(" ")?.mapNotNull { s -> deserialise<Scopes>(s) }?.toSet()

    // TODO - Make sure we validate enough to respond https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.2.1

    return when {

        //
        // Validation Checks - TODO - Add failure reasons
        //

        location.client_id == null -> AuthorisationRequest.Invalid//(invalid_request, "Missing parameter: redirect_uri")

        clientId == null -> AuthorisationRequest.Invalid//(unauthorized_client, "Unauthorized client")

        // TODO - Validate client is authorized to request an authorization code using this method.
        //AuthorisationRequest.Invalid(unauthorized_client, "Unauthorized client")

        location.redirect_uri == null -> AuthorisationRequest.Invalid//(invalid_request, "Missing parameter: redirect_uri")

        // TODO - Validate location.redirect_uri for the given clientId
        //AuthorisationRequest.Invalid(invalid_request, "Invalid parameter: redirect_uri")

        // Support aborting an authorisation via a users choice
        location.resume == false -> AuthorisationRequest.Aborted(location.redirect_uri)

        responseType == null -> AuthorisationRequest.Invalid//(unsupported_response_type, "Unsupported response type: ${location.response_type}")
        location.state == null -> AuthorisationRequest.Invalid//(invalid_request, "Missing parameter: state")

        else -> AuthorisationRequest.Valid(
            responseType = responseType,
            clientId = clientId,
            redirectUri = location.redirect_uri,
            state = location.state,
            requestedScope = scope ?: emptySet()
        )
    }
}