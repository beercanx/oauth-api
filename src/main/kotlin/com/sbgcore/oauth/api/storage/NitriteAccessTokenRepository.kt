package com.sbgcore.oauth.api.storage

import com.sbgcore.oauth.api.openid.ClientId
import com.sbgcore.oauth.api.openid.exchange.tokens.AccessToken
import org.dizitart.kno2.filters.eq
import org.dizitart.kno2.getRepository
import org.dizitart.kno2.nitrite
import org.dizitart.no2.Nitrite
import java.util.*

/**
 * A Nitrite implementation of the [AccessTokenRepository].
 *
 * - https://www.dizitart.org/nitrite-database/
 * - https://www.dizitart.org/nitrite-database/#potassium-nitrite
 */
class NitriteAccessTokenRepository(database: Nitrite) : AccessTokenRepository {

    /**
     * Create a new instance of [NitriteAccessTokenRepository] with an in-memory instance of [Nitrite]
     */
    constructor() : this(
        nitrite(userId = "access-token", password = "XqUX^CUMH90DmK3YdMpNLU#NFE1ioof7")
    )

    private val repository = database.getRepository<AccessToken>()

    override fun insert(new: AccessToken) {
        // TODO - Add async logic to implement automatic deletes, given this is a basic in memory nosql implementation.
        repository.insert(new) // TODO - Verify if we need to assert if token was actually inserted.
    }

    override fun delete(id: UUID) {
        repository.remove(AccessToken::id eq id)
    }

    override fun findById(id: UUID): AccessToken? {
        return repository.find(AccessToken::id eq id).firstOrDefault()
    }

    override fun findByValue(value: UUID): AccessToken? {
        return repository.find(AccessToken::value eq value).firstOrDefault()
    }

    override fun findAllByCustomerId(customerId: Long): Set<AccessToken> {
        return repository.find(AccessToken::customerId eq customerId).toSet()
    }

    override fun findAllByUsername(username: String): Set<AccessToken> {
        return repository.find(AccessToken::username eq username).toSet()
    }

    override fun findAllByClient(clientId: ClientId): Set<AccessToken> {
        return repository.find(AccessToken::clientId eq clientId).toSet()
    }
}