package uk.co.baconi.oauth.api.authorisation

import io.ktor.application.*
import io.ktor.sessions.*
import io.ktor.util.*
import uk.co.baconi.oauth.api.client.ClientId
import uk.co.baconi.oauth.api.enums.deserialise
import uk.co.baconi.oauth.api.ktor.ApplicationContext
import uk.co.baconi.oauth.api.scopes.Scopes
import java.net.URI

/**
 * Validate request based on https://tools.ietf.org/html/rfc6749#section-4.1.1,
 * mixed with some custom logic to support gaining an authentication decision.
 */
fun ApplicationContext.validateAuthorisationRequest(location: AuthorisationLocation): AuthorisationRequest {

    val session = call.sessions.get<AuthorisationSession>()

    val responseType = location.response_type?.let { s -> deserialise<ResponseType>(s) }

    val clientId = location.client_id?.let { s -> deserialise<ClientId>(s) }

    // TODO - Handle if its not a valid URI
    val redirectUri = location.redirect_uri?.let(URI::create)

    // TODO - Make code shared with RawExchangeRequest
    // TODO - Look into reporting invalid or unknown scopes
    val scope = location.scope?.split(" ")?.mapNotNull { s -> deserialise<Scopes>(s) }?.toSet()

    // TODO - Make sure we validate enough to respond https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.2.1

    return when {

        // Support resuming from gaining an authentication decision
        session != null && location.resume == true -> session.request

        // Support aborting an authorisation via a users choice
        session != null && location.resume == false -> AuthorisationRequest.Aborted(session.request.redirectUri)

        //
        // Validation Checks - TODO - Add failure reasons
        //

        location.client_id == null -> AuthorisationRequest.Invalid//(invalid_request, "Missing parameter: redirect_uri")

        clientId == null -> AuthorisationRequest.Invalid//(unauthorized_client, "Unauthorized client")

        // TODO - Validate client is authorized to request an authorization code using this method.
        //AuthorisationRequest.Invalid(unauthorized_client, "Unauthorized client")

        location.redirect_uri == null -> AuthorisationRequest.Invalid//(invalid_request, "Missing parameter: redirect_uri")

        redirectUri == null -> AuthorisationRequest.Invalid//(invalid_request, "Invalid parameter: redirect_uri")

        // TODO - Validate redirectUri for the given clientId
        //AuthorisationRequest.Invalid(invalid_request, "Invalid parameter: redirect_uri")

        responseType == null -> AuthorisationRequest.Invalid//(unsupported_response_type, "Unsupported response type: ${location.response_type}")
        location.state == null -> AuthorisationRequest.Invalid//(invalid_request, "Missing parameter: state")

        else -> AuthorisationRequest.Valid(
            responseType = responseType,
            clientId = clientId,
            redirectUri = redirectUri,
            state = location.state,
            requestedScope = scope ?: emptySet()
        )
    }
}