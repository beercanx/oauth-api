package uk.co.baconi.oauth.api.tokens

import org.dizitart.kno2.filters.eq
import org.dizitart.kno2.getRepository
import org.dizitart.kno2.nitrite
import org.dizitart.no2.IndexOptions.indexOptions
import org.dizitart.no2.IndexType.NonUnique
import org.dizitart.no2.Nitrite
import uk.co.baconi.oauth.api.NitriteExpirationManager
import uk.co.baconi.oauth.api.authentication.AuthenticatedUsername
import uk.co.baconi.oauth.api.client.ClientId
import java.io.Closeable

/**
 * A Nitrite implementation of the [AccessTokenRepository].
 *
 * - https://www.dizitart.org/nitrite-database/
 * - https://www.dizitart.org/nitrite-database/#potassium-nitrite
 */
class NitriteAccessTokenRepository(database: Nitrite) : AccessTokenRepository, Closeable by database {

    private val expirationManager = NitriteExpirationManager<String>()

    /**
     * Create a new instance of [NitriteAccessTokenRepository] with an in-memory instance of [Nitrite]
     */
    constructor() : this(
        nitrite(userId = "access-token", password = "XqUX^CUMH90DmK3YdMpNLU#NFE1ioof7")
    )

    private val repository = database.getRepository<AccessToken> {

        // To support finding all access tokens for a specific username
        createIndex(AccessToken::username.name, indexOptions(NonUnique))

        // To support purging access tokens by client [decommissioned client || compromised client]
        createIndex(AccessToken::clientId.name, indexOptions(NonUnique))
    }

    override fun insert(new: AccessToken) {
        repository.insert(new)
        expirationManager.expireAfter(new.value, new.expiresAt, ::delete)
    }

    override fun delete(id: String) {
        repository.remove(AccessToken::value eq id)
        expirationManager.remove(id)
    }

    override fun findById(id: String): AccessToken? {
        return repository.find(AccessToken::value eq id).firstOrDefault()
    }

    override fun findAllByUsername(username: String): Set<AccessToken> {
        return repository.find(AccessToken::username eq username).toSet()
    }

    override fun findAllByClientId(clientId: ClientId): Set<AccessToken> {
        return repository.find(AccessToken::clientId eq clientId).toSet()
    }
}