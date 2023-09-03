package uk.co.baconi.oauth.api.common.token

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import uk.co.baconi.oauth.api.common.token.RefreshTokenTable.clientId
import uk.co.baconi.oauth.api.common.token.RefreshTokenTable.expiresAt
import uk.co.baconi.oauth.api.common.token.RefreshTokenTable.id
import uk.co.baconi.oauth.api.common.token.RefreshTokenTable.issuedAt
import uk.co.baconi.oauth.api.common.token.RefreshTokenTable.notBefore
import uk.co.baconi.oauth.api.common.token.RefreshTokenTable.scopes
import uk.co.baconi.oauth.api.common.token.RefreshTokenTable.username

class RefreshTokenRepository(db: Database) : TokenRepository<RefreshToken, RefreshTokenTable>(RefreshTokenTable, db) {

    override fun toToken(it: ResultRow) = RefreshToken(
        value = it[id].value,
        username = it[username],
        clientId = it[clientId],
        scopes = it[scopes],
        issuedAt = it[issuedAt],
        expiresAt = it[expiresAt],
        notBefore = it[notBefore],
    )
}