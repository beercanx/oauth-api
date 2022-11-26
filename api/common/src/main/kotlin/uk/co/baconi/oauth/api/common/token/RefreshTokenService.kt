package uk.co.baconi.oauth.api.common.token

import uk.co.baconi.oauth.api.common.authentication.AuthenticatedUsername
import uk.co.baconi.oauth.api.common.client.ClientId
import uk.co.baconi.oauth.api.common.client.ClientPrincipal
import uk.co.baconi.oauth.api.common.scope.Scope
import uk.co.baconi.oauth.api.common.uuid.UUIDSerializer
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

class RefreshTokenService(private val repository: RefreshTokenRepository) {

    private val notBeforeShift = 1L
    private val notBeforeShiftUnit = ChronoUnit.MINUTES

    private val tokenAge = 24L
    private val tokenAgeUnit = ChronoUnit.HOURS

    fun issue(username: AuthenticatedUsername, clientId: ClientId, scopes: Set<Scope>): RefreshToken {

        val issuedAt = Instant.now()

        // Set in the future when we should stop using this token
        val expiresAt = issuedAt.plus(tokenAge, tokenAgeUnit)

        // Set in the past to help with NTP drift
        val notBefore = issuedAt.minus(notBeforeShift, notBeforeShiftUnit)

        return RefreshToken(
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

    fun verify(client: ClientPrincipal, token: String): RefreshToken? {
        return when (val uuid = UUIDSerializer.fromValueOrNull(token)) {
            null -> null
            else -> verify(client, uuid)
        }
    }

    fun verify(client: ClientPrincipal, token: UUID): RefreshToken? {

        val refreshToken = repository.findById(token)

        return when {

            // Is the [token] in the repository?
            refreshToken == null -> null

            // Is the [client] the same as the one that the [token] was issued for?
            refreshToken.clientId != client.id -> null

            // Is being used after its valid?
            refreshToken.hasExpired() -> {
                repository.deleteByRecord(refreshToken)
                null // Unlikely but possible
            }

            // Is being used before its valid?
            refreshToken.isBefore() -> null

            // Looks valid to me 👍
            else -> refreshToken
        }
    }
}