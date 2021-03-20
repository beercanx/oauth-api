package com.sbgcore.oauth.api.openid.exchange.tokens

import com.sbgcore.oauth.api.openid.ClientId
import com.sbgcore.oauth.api.openid.Scopes
import com.sbgcore.oauth.api.storage.AccessTokenRepository
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import java.util.*

class AccessTokenService(private val repository: AccessTokenRepository) {

    private val notBeforeShift = 1L
    private val notBeforeShiftUnit = ChronoUnit.MINUTES

    private val tokenAge = 2L
    private val tokenAgeUnit = ChronoUnit.HOURS

    fun issue(customerId: Long, username: String, clientId: ClientId, scopes: Set<Scopes>): AccessToken {

        val id = UUID.randomUUID()
        val value = UUID.randomUUID()

        val issuedAt = OffsetDateTime.now()

        // Set in the future when we should stop using this token
        val expiresAt = issuedAt.plus(tokenAge, tokenAgeUnit)

        // Set in the past to help with NTP drift
        val notBefore = issuedAt.minus(notBeforeShift, notBeforeShiftUnit)

        return AccessToken(
            id = id,
            value = value,
            customerId = customerId,
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