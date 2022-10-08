package uk.co.baconi.oauth.api.authorisation

import org.dizitart.kno2.filters.eq
import org.dizitart.kno2.getRepository
import org.dizitart.kno2.nitrite
import org.dizitart.no2.IndexOptions.indexOptions
import org.dizitart.no2.IndexType.NonUnique
import org.dizitart.no2.Nitrite
import uk.co.baconi.oauth.api.client.ClientId
import java.io.Closeable

/**
 * A Nitrite implementation of the [AuthorisationCodeRepository].
 *
 * - https://www.dizitart.org/nitrite-database/
 * - https://www.dizitart.org/nitrite-database/#potassium-nitrite
 */
class NitriteAuthorisationCodeRepository(database: Nitrite) : AuthorisationCodeRepository, Closeable by database {

    /**
     * Create a new instance of [NitriteAuthorisationCodeRepository] with an in-memory instance of [Nitrite]
     */
    constructor() : this(
        nitrite(userId = "authorisation-code", password = "yfRGY6x3+-%9KR!tg&@CdZ#xs??Q^AkC")
    )

    private val repository = database.getRepository<AuthorisationCode>()

    override fun insert(new: AuthorisationCode) {
        repository.insert(new)
    }

    override fun delete(id: String) {
        repository.remove(AuthorisationCode::value eq id)
    }

    override fun findById(id: String): AuthorisationCode? {
        return repository.find(AuthorisationCode::value eq id).firstOrDefault()
    }
}