package uk.co.baconi.oauth.api.common.token

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.transactions.transaction
import uk.co.baconi.oauth.api.common.authentication.AuthenticatedUsername
import uk.co.baconi.oauth.api.common.client.ClientId
import uk.co.baconi.oauth.api.common.scope.Scope
import uk.co.baconi.oauth.api.common.scope.ScopesDeserializer
import uk.co.baconi.oauth.api.common.scope.ScopesSerializer
import java.time.Instant
import java.util.*

class AccessTokenRepository(private val database: Database) {

    fun insert(new: AccessToken) {
        transaction(database) {
            AccessTokenTable.insertAndGetId {
                it[id] = new.value
                it[username] = new.username
                it[clientId] = new.clientId
                it[scopes] = new.scopes.let(ScopesSerializer::serialize)
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

    fun findAllByUsername(username: AuthenticatedUsername): List<AccessToken> {
        return transaction(database) {
            AccessTokenTable
                .select { AccessTokenTable.username eq username }
                .map(::toAccessToken)
        }
    }

    fun findAllByClientId(clientId: ClientId): List<AccessToken> {
        return transaction(database) {
            AccessTokenTable
                .select { AccessTokenTable.clientId eq clientId }
                .map(::toAccessToken)
        }
    }

    fun deleteById(id: UUID) {
        transaction(database) {
            AccessTokenTable.deleteWhere { this.id eq id }
        }
    }

    fun deleteByRecord(record: AccessToken) {
        transaction(database) {
            AccessTokenTable.deleteWhere { this.id eq record.value }
        }
    }

    fun deleteExpired() {
        transaction(database) {
            AccessTokenTable.deleteWhere { this.expiresAt lessEq Instant.now() }
        }
    }

    private fun toAccessToken(it: ResultRow): AccessToken {
        return AccessToken(
            value = it[AccessTokenTable.id].value,
            username = it[AccessTokenTable.username],
            clientId = it[AccessTokenTable.clientId],
            scopes = it[AccessTokenTable.scopes].let(ScopesDeserializer::deserialize).map(::Scope).toSet(),
            issuedAt = it[AccessTokenTable.issuedAt],
            expiresAt = it[AccessTokenTable.expiresAt],
            notBefore = it[AccessTokenTable.notBefore],
        )
    }
}