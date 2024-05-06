package uk.co.baconi.oauth.api.common.authorisation

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.timestamp
import uk.co.baconi.oauth.api.common.authentication.AuthenticatedUsername
import uk.co.baconi.oauth.api.common.authentication.authenticatedUsernameColumn
import uk.co.baconi.oauth.api.common.client.ClientId
import uk.co.baconi.oauth.api.common.client.clientIdColumn
import uk.co.baconi.oauth.api.common.scope.Scope
import uk.co.baconi.oauth.api.common.scope.scopeColumn
import uk.co.baconi.oauth.api.common.token.AccessTokenTable.index
import java.time.Instant
import java.util.*

object AuthorisationCodeTable : IdTable<UUID>() {

    // Max lengths of direct user based input, see [AuthorisationRequestValidation]
    const val REDIRECT_URI_LENGTH = 255
    const val STATE_LENGTH = 255
    const val CODE_CHALLENGE_LENGTH = 255

    /**
     * [AuthorisationCode.value]
     */
    override val id: Column<EntityID<UUID>> = uuid("id").entityId()

    val type: Column<String> = varchar("type", 6) // basic|pkce
    val used: Column<Boolean> = bool("used")
    val username: Column<AuthenticatedUsername> = authenticatedUsernameColumn("username").index()
    val clientId: Column<ClientId> = clientIdColumn("client_id")
    val issuedAt: Column<Instant> = timestamp("issued_at") // TODO - Verify Instant over LocalDateTime as Instant still seems like its persisting in local time not UTC
    val expiresAt: Column<Instant> = timestamp("expires_at").index()
    val scopes: Column<Set<Scope>> = scopeColumn("scopes") // TODO - Consider full relational DB setup here instead.
    val redirectUri: Column<String> = varchar("redirect_uri", REDIRECT_URI_LENGTH)
    val state: Column<String> = varchar("state", STATE_LENGTH)
    val codeChallenge: Column<String?> = varchar("code_challenge", CODE_CHALLENGE_LENGTH).nullable()
    val codeChallengeMethod: Column<String?> = varchar("code_challenge_method", 5).nullable()

    override val primaryKey = PrimaryKey(id)
}