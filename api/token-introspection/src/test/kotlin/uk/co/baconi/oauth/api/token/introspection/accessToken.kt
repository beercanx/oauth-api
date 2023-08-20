package uk.co.baconi.oauth.api.token.introspection

import uk.co.baconi.oauth.api.common.authentication.AuthenticatedUsername
import uk.co.baconi.oauth.api.common.client.ClientId
import uk.co.baconi.oauth.api.common.scope.Scope
import uk.co.baconi.oauth.api.common.token.AccessToken
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

internal fun accessToken(
    value: UUID = UUID.randomUUID(),
    username: String = "aardvark",
    clientId: String = "badger",
    scopes: Set<Scope> = setOf(Scope.Basic, Scope.ProfileRead, Scope.ProfileWrite),
    now: Instant = Instant.now()
) = AccessToken(
    value = value,
    username = AuthenticatedUsername(username),
    clientId = ClientId(clientId),
    scopes = scopes,
    issuedAt = now,
    expiresAt = now.plus(1, ChronoUnit.DAYS),
    notBefore = now.minus(1, ChronoUnit.DAYS)
)