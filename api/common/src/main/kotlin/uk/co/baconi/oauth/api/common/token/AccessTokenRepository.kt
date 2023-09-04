package uk.co.baconi.oauth.api.common.token

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import uk.co.baconi.oauth.api.common.token.AccessTokenTable.clientId
import uk.co.baconi.oauth.api.common.token.AccessTokenTable.expiresAt
import uk.co.baconi.oauth.api.common.token.AccessTokenTable.id
import uk.co.baconi.oauth.api.common.token.AccessTokenTable.issuedAt
import uk.co.baconi.oauth.api.common.token.AccessTokenTable.notBefore
import uk.co.baconi.oauth.api.common.token.AccessTokenTable.scopes
import uk.co.baconi.oauth.api.common.token.AccessTokenTable.username

class AccessTokenRepository(db: Database) : TokenRepository<AccessToken, AccessTokenTable>(AccessTokenTable, db) {

    override fun toToken(it: ResultRow): AccessToken {
        return AccessToken(
            value = it[id].value,
            username = it[username],
            clientId = it[clientId],
            scopes = it[scopes],
            issuedAt = it[issuedAt],
            expiresAt = it[expiresAt],
            notBefore = it[notBefore],
        )
    }
}