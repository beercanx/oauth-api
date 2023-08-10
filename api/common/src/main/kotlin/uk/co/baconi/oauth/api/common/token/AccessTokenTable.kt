package uk.co.baconi.oauth.api.common.token

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.timestamp
import uk.co.baconi.oauth.api.common.scope.Scope
import java.time.Instant
import java.util.*

object AccessTokenTable : IdTable<UUID>() {

    /**
     * [AccessToken.value]
     */
    override val id: Column<EntityID<UUID>> = uuid("id").entityId()

    val username: Column<String> = varchar("username", 50).index()
    val clientId: Column<String> = varchar("client_id", 25).index()
    val scopes: Column<String> = varchar("scopes", calculateMaxScopeFieldLength())
    val issuedAt: Column<Instant> = timestamp("issued_at") // TODO - Verify Instant over LocalDateTime as Instant still seems like its persisting in local time not UTC
    val expiresAt: Column<Instant> = timestamp("expires_at").index()
    val notBefore: Column<Instant> = timestamp("not_before")

    override val primaryKey = PrimaryKey(id)

    /**
     * Assuming serialisation is via a space delimited string calculate the max length of the scope field.
     */
    private fun calculateMaxScopeFieldLength(): Int {
        val scopes = Scope.entries.toTypedArray()
        val gapSize = scopes.size - 1
        val scopeSize = scopes.fold(0) { size, scope ->
            size + scope.value.length
        }
        return gapSize + scopeSize
    }
}