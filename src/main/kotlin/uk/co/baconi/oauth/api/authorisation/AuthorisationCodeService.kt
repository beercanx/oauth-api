package uk.co.baconi.oauth.api.authorisation

import uk.co.baconi.oauth.api.authentication.AuthenticatedSession
import java.time.OffsetDateTime
import java.util.*

class AuthorisationCodeService(private val repository: AuthorisationCodeRepository) {

    fun issue(request: AuthorisationRequest.Valid, authenticated: AuthenticatedSession) : AuthorisationCode {

        return AuthorisationCode(
            value = UUID.randomUUID().toString(),
            issuedAt = OffsetDateTime.now(),
            clientId = request.clientId,
            username = authenticated.username,
            redirectUri = request.redirectUri,
            requestedScope = request.requestedScope
        ).also(
            repository::insert
        )
    }
}
