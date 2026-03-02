package uk.co.baconi.oauth.api.common.token

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IdTable
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.java.javaUUID
import org.jetbrains.exposed.v1.javatime.timestamp
import uk.co.baconi.oauth.api.common.authentication.AuthenticatedUsername
import uk.co.baconi.oauth.api.common.authentication.authenticatedUsernameColumn
import uk.co.baconi.oauth.api.common.client.ClientId
import uk.co.baconi.oauth.api.common.client.clientIdColumn
import uk.co.baconi.oauth.api.common.scope.Scope
import uk.co.baconi.oauth.api.common.scope.scopeColumn
import java.time.Instant
import java.util.*

object RefreshTokenTable : IdTable<UUID>(), TokenTable {

    /**
     * [RefreshToken.value]
     */
    override val id: Column<EntityID<UUID>> = javaUUID("id").entityId() // TODO - Migrate to Kotlin.UUID

    override val username: Column<AuthenticatedUsername> = authenticatedUsernameColumn("username").index()
    override val clientId: Column<ClientId> = clientIdColumn("client_id").index()
    override val scopes: Column<Set<Scope>> = scopeColumn("scopes") // TODO - Consider full relational DB setup here instead.
    override val issuedAt: Column<Instant> = timestamp("issued_at") // TODO - Verify Instant over LocalDateTime as Instant still seems like its persisting in local time not UTC
    override val expiresAt: Column<Instant> = timestamp("expires_at").index()
    override val notBefore: Column<Instant> = timestamp("not_before")

    override val primaryKey = PrimaryKey(id)
}