package uk.co.baconi.oauth.api.common.token

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Column
import uk.co.baconi.oauth.api.common.authentication.AuthenticatedUsername
import uk.co.baconi.oauth.api.common.client.ClientId
import uk.co.baconi.oauth.api.common.scope.Scope
import java.time.Instant
import java.util.*

interface TokenTable {
    val id: Column<EntityID<UUID>>
    val username: Column<AuthenticatedUsername>
    val clientId: Column<ClientId>
    val scopes: Column<Set<Scope>>
    val issuedAt: Column<Instant>
    val expiresAt: Column<Instant>
    val notBefore: Column<Instant>
}