package uk.co.baconi.oauth.api.authorisation

import uk.co.baconi.oauth.api.authentication.AuthenticatedSession
import uk.co.baconi.oauth.api.client.ConfidentialClient
import uk.co.baconi.oauth.api.client.PublicClient
import uk.co.baconi.oauth.api.exchange.InvalidConfidentialExchangeRequest
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import java.util.*

class AuthorisationCodeService(private val repository: AuthorisationCodeRepository) {

    private val age = 1L
    private val ageUnit = ChronoUnit.MINUTES

    fun issue(request: AuthorisationRequest.Valid, authenticated: AuthenticatedSession) : AuthorisationCode {

        // What time is it Mr Wolf?
        val issuedAt = OffsetDateTime.now()

        // Set in the future when we should stop accepting this authorisation code
        val expiresAt = issuedAt.plus(age, ageUnit)

        return AuthorisationCode(
            value = UUID.randomUUID().toString(),
            issuedAt = issuedAt,
            expiresAt = expiresAt,
            clientId = request.clientId,
            username = authenticated.username,
            redirectUri = request.redirectUri,
            requestedScope = request.requestedScope
        ).also(
            repository::insert
        )
    }

    fun validate(client: ConfidentialClient, code: String, redirectUri: String): AuthorisationCode? {

        val authorisationCode = repository.findById(code)

        return when {

            // Validate the [code] is a valid code via a repository
            authorisationCode == null -> null

            // Validate the [client_id] is the same as what was used to generate the [code]
            authorisationCode.clientId != client.id -> null

            // Validate the [redirect_uri] is the same as what was used to generate the [code]
            authorisationCode.redirectUri != redirectUri -> null

            // Verify the authorisation code has not yet expired.
            authorisationCode.hasExpired() -> null

            // Replace code with AuthorisationCode object
            else -> authorisationCode
        }
    }

    fun validate(client: PublicClient, code: String, redirectUri: String, codeVerifier: String): AuthorisationCode? {
        // TODO - Implement
        return null;
    }
}
