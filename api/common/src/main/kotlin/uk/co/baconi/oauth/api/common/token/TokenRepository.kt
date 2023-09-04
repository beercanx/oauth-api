package uk.co.baconi.oauth.api.common.token

import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.transactions.transaction
import uk.co.baconi.oauth.api.common.authentication.AuthenticatedUsername
import uk.co.baconi.oauth.api.common.client.ClientId
import java.time.Instant
import java.util.*

abstract class TokenRepository<R, T>(private val tokenTable: T, private val database: Database)
        where R : Token,
              T : TokenTable,
              T : IdTable<UUID> {

    protected abstract fun toToken(it: ResultRow): R

    fun insert(new: R) {
        transaction(database) {
            tokenTable.insertAndGetId {
                it[tokenTable.id] = new.value
                it[tokenTable.username] = new.username
                it[tokenTable.clientId] = new.clientId
                it[tokenTable.scopes] = new.scopes
                it[tokenTable.issuedAt] = new.issuedAt
                it[tokenTable.expiresAt] = new.expiresAt
                it[tokenTable.notBefore] = new.notBefore
            }
        }
    }

    fun findById(id: UUID): R? {
        return transaction(database) {
            tokenTable
                .select { tokenTable.id eq id }
                .firstOrNull()
                ?.let(::toToken)
        }
    }

    fun findAllByUsername(username: AuthenticatedUsername): List<R> {
        return transaction(database) {
            tokenTable
                .select { tokenTable.username eq username }
                .map(::toToken)
        }
    }

    fun findAllByClientId(clientId: ClientId): List<R> {
        return transaction(database) {
            tokenTable
                .select { tokenTable.clientId eq clientId }
                .map(::toToken)
        }
    }

    fun deleteById(id: UUID) {
        transaction(database) {
            tokenTable.deleteWhere { this.id eq id }
        }
    }

    fun deleteByRecord(record: R) {
        transaction(database) {
            tokenTable.deleteWhere { this.id eq record.value }
        }
    }

    fun deleteExpired() {
        transaction(database) {
            tokenTable.deleteWhere { this.expiresAt lessEq Instant.now() }
        }
    }
}