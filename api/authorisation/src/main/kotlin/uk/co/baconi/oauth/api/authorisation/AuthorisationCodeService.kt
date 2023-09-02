package uk.co.baconi.oauth.api.authorisation

import uk.co.baconi.oauth.api.common.authentication.AuthenticatedUsername
import uk.co.baconi.oauth.api.common.authorisation.AuthorisationCode
import uk.co.baconi.oauth.api.common.authorisation.AuthorisationCodeRepository
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

class AuthorisationCodeService(private val repository: AuthorisationCodeRepository) {

    private val age = 1L
    private val ageUnit = ChronoUnit.MINUTES

    fun issue(request: AuthorisationRequest.Valid, username: AuthenticatedUsername): AuthorisationCode {

        // What time is it Mr Wolf?
        val issuedAt = Instant.now()

        // Set in the future when we should stop accepting this authorisation code
        val expiresAt = issuedAt.plus(age, ageUnit)

        val basic = AuthorisationCode.Basic(
            value = UUID.randomUUID(),
            issuedAt = issuedAt,
            expiresAt = expiresAt,
            clientId = request.clientId,
            username = username,
            redirectUri = request.redirectUri,
            scopes = request.scopes,
            state = request.state
        )

        return when (request) {
            is AuthorisationRequest.Basic -> basic
            is AuthorisationRequest.PKCE -> AuthorisationCode.PKCE(
                base = basic,
                codeChallenge = request.codeChallenge,
                codeChallengeMethod = request.codeChallengeMethod
            )
        }.also(
            repository::insert
        )
    }
}