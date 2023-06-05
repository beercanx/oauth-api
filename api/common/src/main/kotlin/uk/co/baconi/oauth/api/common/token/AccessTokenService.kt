package uk.co.baconi.oauth.api.common.token

import uk.co.baconi.oauth.api.common.authentication.AuthenticatedUsername
import uk.co.baconi.oauth.api.common.client.ClientId
import uk.co.baconi.oauth.api.common.scope.Scope
import uk.co.baconi.oauth.api.common.uuid.UUIDSerializer
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

class AccessTokenService(private val repository: AccessTokenRepository) {

    private val notBeforeShift = 1L
    private val notBeforeShiftUnit = ChronoUnit.MINUTES

    private val tokenAge = 2L
    private val tokenAgeUnit = ChronoUnit.HOURS

    fun issue(username: AuthenticatedUsername, clientId: ClientId, scopes: Set<Scope>): AccessToken {

        val issuedAt = Instant.now()

        // Set in the future when we should stop using this token
        val expiresAt = issuedAt.plus(tokenAge, tokenAgeUnit)

        // Set in the past to help with NTP drift
        val notBefore = issuedAt.minus(notBeforeShift, notBeforeShiftUnit)

        return AccessToken(
            value = UUID.randomUUID(),
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

    fun authenticate(token: String): AccessToken? {
        return when (val uuid = UUIDSerializer.fromValueOrNull(token)) {
            null -> null
            else -> authenticate(uuid)
        }
    }

    fun authenticate(token: UUID): AccessToken? {

        val accessToken = repository.findById(token)

        return when {
            accessToken == null -> null
            accessToken.hasExpired() -> { // Is being used after its valid.
                repository.deleteByRecord(accessToken)
                null // Unlikely but possible
            }
            accessToken.isBefore() -> null // Is being used before its valid.
            else -> accessToken
        }
    }
}