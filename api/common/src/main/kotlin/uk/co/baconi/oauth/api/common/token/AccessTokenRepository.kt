package uk.co.baconi.oauth.api.common.token

import org.jetbrains.exposed.sql.*
import uk.co.baconi.oauth.api.common.Repository
import uk.co.baconi.oauth.api.common.Repository.*
import uk.co.baconi.oauth.api.common.authentication.AuthenticatedUsername
import uk.co.baconi.oauth.api.common.client.ClientId
import uk.co.baconi.oauth.api.common.scope.Scope
import java.util.*
import kotlin.sequences.Sequence

class AccessTokenRepository : Repository<AccessToken, UUID>, WithInsert<AccessToken>, WithDelete<AccessToken, UUID> {

    override fun insert(new: AccessToken) {
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

    override fun findById(id: UUID): AccessToken? {
        return AccessTokenTable
            .select { AccessTokenTable.id eq id }
            .asSequence()
            .map(::toAccessToken)
            .firstOrNull()
    }

    fun findAllByUsername(username: String): Sequence<AccessToken> {
        return AccessTokenTable
            .select { AccessTokenTable.username eq username }
            .asSequence()
            .map(::toAccessToken)
    }

    fun findAllByClientId(clientId: ClientId): Sequence<AccessToken> {
        return AccessTokenTable
            .select { AccessTokenTable.clientId eq clientId.value }
            .asSequence()
            .map(::toAccessToken)
    }

    override fun deleteById(id: UUID) {
        AccessTokenTable.deleteWhere { AccessTokenTable.id eq id }
    }

    override fun deleteByRecord(record: AccessToken) {
        AccessTokenTable.deleteWhere { AccessTokenTable.id eq record.value }
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