package uk.co.baconi.oauth.api.common.authorisation

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import uk.co.baconi.oauth.api.common.client.ClientId
import uk.co.baconi.oauth.api.common.scope.ScopesSerializer
import uk.co.baconi.oauth.api.common.authentication.AuthenticatedUsername
import java.time.Instant
import java.util.*

class AuthorisationCodeRepository(private val database: Database) {

    // TODO - Consider automatic expiration of token records.

    companion object {
        private const val BASIC = "basic"
        private const val PKCE = "pkce"
    }

    fun insert(new: AuthorisationCode) {
        transaction(database) {
            AuthorisationCodeTable.insertAndGetId {
                it[id] = new.value
                when (new) {
                    is AuthorisationCode.Basic -> {
                        it[type] = BASIC
                    }
                    is AuthorisationCode.PKCE -> {
                        it[type] = PKCE
                        it[codeChallenge] = new.codeChallenge.value
                        it[codeChallengeMethod] = new.codeChallengeMethod.name
                    }
                }
                it[username] = new.username.value
                it[clientId] = new.clientId.value
                it[issuedAt] = new.issuedAt
                it[expiresAt] = new.expiresAt
                it[scopes] = new.scopes.let(ScopesSerializer::serialize)
                it[redirectUri] = new.redirectUri
                it[state] = new.state
            }
        }
    }

    fun findById(id: UUID): AuthorisationCode? {
        return transaction(database) {
            AuthorisationCodeTable
                .select { AuthorisationCodeTable.id eq id }
                .firstOrNull()
                ?.let(::toAuthorisationCode)
        }
    }

    fun deleteById(id: UUID) {
        transaction(database) {
            AuthorisationCodeTable.deleteWhere { AuthorisationCodeTable.id eq id }
        }
    }

    fun deleteExpired() {
        transaction(database) {
            AuthorisationCodeTable.deleteWhere { AuthorisationCodeTable.expiresAt lessEq Instant.now() }
        }
    }

    private fun toAuthorisationCode(it: ResultRow): AuthorisationCode {
        return when (val type = it[AuthorisationCodeTable.type]) {
            BASIC -> AuthorisationCode.Basic(
                value = it[AuthorisationCodeTable.id].value,
                username = it[AuthorisationCodeTable.username].let(::AuthenticatedUsername),
                clientId = it[AuthorisationCodeTable.clientId].let(::ClientId),
                issuedAt = it[AuthorisationCodeTable.issuedAt],
                expiresAt = it[AuthorisationCodeTable.expiresAt],
                scopes = it[AuthorisationCodeTable.scopes].let(ScopesSerializer::deserialize),
                redirectUri = it[AuthorisationCodeTable.redirectUri],
                state = it[AuthorisationCodeTable.state],
            )
            PKCE -> AuthorisationCode.PKCE(
                value = it[AuthorisationCodeTable.id].value,
                username = it[AuthorisationCodeTable.username].let(::AuthenticatedUsername),
                clientId = it[AuthorisationCodeTable.clientId].let(::ClientId),
                issuedAt = it[AuthorisationCodeTable.issuedAt],
                expiresAt = it[AuthorisationCodeTable.expiresAt],
                scopes = it[AuthorisationCodeTable.scopes].let(ScopesSerializer::deserialize),
                redirectUri = it[AuthorisationCodeTable.redirectUri],
                state = it[AuthorisationCodeTable.state],
                codeChallenge = CodeChallenge(checkNotNull(it[AuthorisationCodeTable.codeChallenge])),
                codeChallengeMethod = enumValueOf(checkNotNull(it[AuthorisationCodeTable.codeChallengeMethod])),
            )
            else -> throw IllegalStateException("Unknown authorisation code type: $type")
        }
    }
}