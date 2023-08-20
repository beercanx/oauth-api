package uk.co.baconi.oauth.api.common.token

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.timestamp
import uk.co.baconi.oauth.api.common.authentication.AuthenticatedUsername
import uk.co.baconi.oauth.api.common.authentication.authenticatedUsernameColumn
import uk.co.baconi.oauth.api.common.client.ClientId
import uk.co.baconi.oauth.api.common.client.clientIdColumn
import uk.co.baconi.oauth.api.common.scope.ScopeRepository
import java.time.Instant
import java.util.*

object RefreshTokenTable : IdTable<UUID>() {

    /**
     * [RefreshToken.value]
     */
    override val id: Column<EntityID<UUID>> = uuid("id").entityId()

    val username: Column<AuthenticatedUsername> = authenticatedUsernameColumn().index()
    val clientId: Column<ClientId> = clientIdColumn().index()
    val scopes: Column<String> = varchar(
        "scopes",
        ScopeRepository.maxScopeFieldLength
    )  // TODO - Consider true DB style, with a table of scopes and references in a list format
    val issuedAt: Column<Instant> = timestamp("issued_at") // TODO - Verify Instant over LocalDateTime as Instant still seems like its persisting in local time not UTC
    val expiresAt: Column<Instant> = timestamp("expires_at").index()
    val notBefore: Column<Instant> = timestamp("not_before")

    override val primaryKey = PrimaryKey(id)
}