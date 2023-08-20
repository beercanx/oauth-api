package uk.co.baconi.oauth.api.common.authorisation

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

object AuthorisationCodeTable : IdTable<UUID>() {

    /**
     * [AuthorisationCode.value]
     */
    override val id: Column<EntityID<UUID>> = uuid("id").entityId()

    val type: Column<String> = varchar("type", 6) // basic|pkce
    val username: Column<AuthenticatedUsername> = authenticatedUsernameColumn()
    val clientId: Column<ClientId> = clientIdColumn()
    val issuedAt: Column<Instant> = timestamp("issued_at") // TODO - Verify Instant over LocalDateTime as Instant still seems like its persisting in local time not UTC
    val expiresAt: Column<Instant> = timestamp("expires_at").index()
    val scopes: Column<String> = varchar(
        "scopes",
        ScopeRepository.maxScopeFieldLength
    )  // TODO - Consider true DB style, with a table of scopes and references in a list format
    val redirectUri: Column<String> = varchar("redirect_uri", 255)
    val state: Column<String> = varchar("state", 255)
    val codeChallenge: Column<String?> = varchar("code_challenge", 255).nullable()
    val codeChallengeMethod: Column<String?> = varchar("code_challenge_method", 255).nullable()

    override val primaryKey = PrimaryKey(id)
}