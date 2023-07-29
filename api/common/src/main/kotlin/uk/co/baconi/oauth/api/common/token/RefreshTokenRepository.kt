package uk.co.baconi.oauth.api.common.token

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.transactions.transaction
import uk.co.baconi.oauth.api.common.client.ClientId
import uk.co.baconi.oauth.api.common.scope.ScopesSerializer
import uk.co.baconi.oauth.api.common.authentication.AuthenticatedUsername
import java.time.Instant
import java.util.*

class RefreshTokenRepository(private val database: Database) {

    fun insert(new: RefreshToken) {
        transaction(database) {
            RefreshTokenTable.insertAndGetId {
                it[id] = new.value
                it[username] = new.username.value
                it[clientId] = new.clientId.value
                it[scopes] = new.scopes.let(ScopesSerializer::serialize)
                it[issuedAt] = new.issuedAt
                it[expiresAt] = new.expiresAt
                it[notBefore] = new.notBefore
            }
        }
    }

    fun findById(id: UUID): RefreshToken? {
        return transaction(database) {
            RefreshTokenTable
                .select { RefreshTokenTable.id eq id }
                .firstOrNull()
                ?.let(::toRefreshToken)
        }
    }

    fun findAllByUsername(username: String): List<RefreshToken> {
        return transaction(database) {
            RefreshTokenTable
                .select { RefreshTokenTable.username eq username }
                .map(::toRefreshToken)
        }
    }

    fun findAllByClientId(clientId: ClientId): List<RefreshToken> {
        return transaction(database) {
            RefreshTokenTable
                .select { RefreshTokenTable.clientId eq clientId.value }
                .map(::toRefreshToken)
        }
    }

    fun deleteById(id: UUID) {
        transaction(database) {
            RefreshTokenTable.deleteWhere { this.id eq id }
        }
    }

    fun deleteByRecord(record: RefreshToken) {
        transaction(database) {
            RefreshTokenTable.deleteWhere { this.id eq record.value }
        }
    }

    fun deleteExpired() {
        transaction(database) {
            RefreshTokenTable.deleteWhere { this.expiresAt lessEq Instant.now() }
        }
    }

    private fun toRefreshToken(it: ResultRow): RefreshToken {
        return RefreshToken(
            value = it[RefreshTokenTable.id].value,
            username = it[RefreshTokenTable.username].let(::AuthenticatedUsername),
            clientId = it[RefreshTokenTable.clientId].let(::ClientId),
            scopes = it[RefreshTokenTable.scopes].let(ScopesSerializer::deserialize),
            issuedAt = it[RefreshTokenTable.issuedAt],
            expiresAt = it[RefreshTokenTable.expiresAt],
            notBefore = it[RefreshTokenTable.notBefore],
        )
    }
}