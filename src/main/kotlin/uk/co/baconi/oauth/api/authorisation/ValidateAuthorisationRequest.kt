package uk.co.baconi.oauth.api.authorisation

import io.ktor.application.*
import io.ktor.sessions.*
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
    val scope = location.scope?.split(" ")?.mapNotNull { s -> deserialise<Scopes>(s) }?.toSet()

    return when {

        // Support resuming from gaining an authentication decision
        session != null && location.resume == true -> session.request

        // Validation Checks - TODO - Add failure reasons
        responseType == null -> AuthorisationRequest.Invalid
        clientId == null -> AuthorisationRequest.Invalid
        redirectUri == null -> AuthorisationRequest.Invalid
        location.state == null -> AuthorisationRequest.Invalid

        else -> AuthorisationRequest.Valid(
            responseType = responseType,
            clientId = clientId,
            redirectUri = redirectUri,
            state = location.state,
            requestedScope = scope ?: emptySet()
        )
    }
}