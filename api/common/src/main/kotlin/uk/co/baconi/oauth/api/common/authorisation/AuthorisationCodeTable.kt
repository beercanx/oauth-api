package uk.co.baconi.oauth.api.common.authorisation

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.timestamp
import uk.co.baconi.oauth.api.common.scope.Scope
import java.time.Instant
import java.util.*

object AuthorisationCodeTable : IdTable<UUID>() {

    /**
     * [AuthorisationCode.value]
     */
    override val id: Column<EntityID<UUID>> = uuid("id").entityId()

    val type: Column<String> = varchar("type", 6) // basic|pkce
    val username: Column<String> = varchar("username", 50)
    val clientId: Column<String> = varchar("client_id", 25)
    val issuedAt: Column<Instant> = timestamp("issued_at") // TODO - Verify Instant over LocalDateTime as Instant still seems like its persisting in local time not UTC
    val expiresAt: Column<Instant> = timestamp("expires_at").index()
    val scopes: Column<String> = varchar("scopes", calculateMaxScopeFieldLength())
    val redirectUri: Column<String> = varchar("redirect_uri", 255)
    val state: Column<String?> = varchar("state", 255).nullable()
    val codeChallenge: Column<String?> = varchar("code_challenge", 255).nullable()
    val codeChallengeMethod: Column<String?> = varchar("code_challenge_method", 255).nullable()

    override val primaryKey = PrimaryKey(id)

    /**
     * Assuming serialisation is via a space delimited string calculate the max length of the scope field.
     */
    private fun calculateMaxScopeFieldLength(): Int {
        val scopes = Scope.entries.toTypedArray()
        val gapSize = scopes.size
        val scopeSize = scopes.fold(0) { size, scope ->
            size + scope.value.length
        }
        return gapSize + scopeSize
    }
}