package uk.co.baconi.oauth.api.tokens

import uk.co.baconi.oauth.api.client.ClientId
import uk.co.baconi.oauth.api.authentication.AuthenticatedUsername
import uk.co.baconi.oauth.api.scopes.Scopes
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import java.util.*

class AccessTokenService(private val repository: AccessTokenRepository) {

    private val notBeforeShift = 1L
    private val notBeforeShiftUnit = ChronoUnit.MINUTES

    private val tokenAge = 2L
    private val tokenAgeUnit = ChronoUnit.HOURS

    fun issue(username: AuthenticatedUsername, clientId: ClientId, scopes: Set<Scopes>): AccessToken {

        val value = UUID.randomUUID().toString()

        val issuedAt = OffsetDateTime.now()

        // Set in the future when we should stop using this token
        val expiresAt = issuedAt.plus(tokenAge, tokenAgeUnit)

        // Set in the past to help with NTP drift
        val notBefore = issuedAt.minus(notBeforeShift, notBeforeShiftUnit)

        return AccessToken(
            value = value,
            username = username,
            clientId = clientId,
            scopes = scopes,
            issuedAt = issuedAt,
            expiresAt = expiresAt,
            notBefore = notBefore
        ).also(
            repository::insert
        )
    }
}