package uk.co.baconi.oauth.api.common.token

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import uk.co.baconi.oauth.api.common.DatabaseModule
import uk.co.baconi.oauth.api.common.authentication.AuthenticatedUsername
import uk.co.baconi.oauth.api.common.client.ClientId
import uk.co.baconi.oauth.api.common.scope.Scope
import java.util.*

class AccessTokenRepository(private val database: Database) {

    constructor() : this(DatabaseModule.accessTokenDatabase)

    fun insert(new: AccessToken) {
        transaction(database) {
            AccessTokenTable.insertAndGetId {
                it[id] = new.value
                it[username] = new.username.value
                it[clientId] = new.clientId.value
                it[scopes] = new.scopes.joinToString(separator = " ", transform = Scope::value)
                it[issuedAt] = new.issuedAt
                it[expiresAt] = new.expiresAt
                it[notBefore] = new.notBefore
            }
        }
    }

    fun findById(id: UUID): AccessToken? {
        return transaction(database) {
            AccessTokenTable
                .select { AccessTokenTable.id eq id }
                .firstOrNull()
                ?.let(::toAccessToken)
        }
    }

    fun findAllByUsername(username: String): List<AccessToken> {
        return transaction(database) {
            AccessTokenTable
                .select { AccessTokenTable.username eq username }
                .map(::toAccessToken)
        }
    }

    fun findAllByClientId(clientId: ClientId): List<AccessToken> {
        return transaction(database) {
            AccessTokenTable
                .select { AccessTokenTable.clientId eq clientId.value }
                .map(::toAccessToken)
        }
    }

    fun deleteById(id: UUID) {
        transaction(database) {
            AccessTokenTable.deleteWhere { AccessTokenTable.id eq id }
        }
    }

    fun deleteByRecord(record: AccessToken) {
        transaction(database) {
            AccessTokenTable.deleteWhere { AccessTokenTable.id eq record.value }
        }
    }

    private fun toAccessToken(it: ResultRow): AccessToken {
        return AccessToken(
            value = it[AccessTokenTable.id].value,
            username = it[AccessTokenTable.username].let(::AuthenticatedUsername),
            clientId = it[AccessTokenTable.clientId].let(::ClientId),
            scopes = it[AccessTokenTable.scopes].split(" ").map(Scope::fromValue).toSet(),
            issuedAt = it[AccessTokenTable.issuedAt],
            expiresAt = it[AccessTokenTable.expiresAt],
            notBefore = it[AccessTokenTable.notBefore],
        )
    }
}