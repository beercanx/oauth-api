package uk.co.baconi.oauth.api.common.token

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.timestamp
import uk.co.baconi.oauth.api.common.authentication.AuthenticatedUsername
import uk.co.baconi.oauth.api.common.authentication.authenticatedUsernameColumn
import uk.co.baconi.oauth.api.common.client.ClientId
import uk.co.baconi.oauth.api.common.client.clientIdColumn
import uk.co.baconi.oauth.api.common.scope.Scope
import uk.co.baconi.oauth.api.common.scope.ScopeRepository
import uk.co.baconi.oauth.api.common.scope.scopeColumn
import java.time.Instant
import java.util.*

object AccessTokenTable : IdTable<UUID>(), TokenTable {

    /**
     * [AccessToken.value]
     */
    override val id: Column<EntityID<UUID>> = uuid("id").entityId()

    override val username: Column<AuthenticatedUsername> = authenticatedUsernameColumn("username").index()
    override val clientId: Column<ClientId> = clientIdColumn("client_id").index()
    override val scopes: Column<Set<Scope>> = scopeColumn("scopes") // TODO - Consider full relational DB setup here instead.
    override val issuedAt: Column<Instant> = timestamp("issued_at") // TODO - Verify Instant over LocalDateTime as Instant still seems like its persisting in local time not UTC
    override val expiresAt: Column<Instant> = timestamp("expires_at").index()
    override val notBefore: Column<Instant> = timestamp("not_before")

    override val primaryKey = PrimaryKey(id)
}