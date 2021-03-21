package com.sbgcore.oauth.api.authentication

import com.sbgcore.oauth.api.openid.ClientId
import com.sbgcore.oauth.api.openid.ClientId.ConsumerX
import com.sbgcore.oauth.api.openid.ClientId.ConsumerZ
import org.bouncycastle.crypto.generators.OpenBSDBCrypt
import org.dizitart.kno2.filters.eq
import org.dizitart.kno2.getRepository
import org.dizitart.kno2.nitrite
import org.dizitart.no2.IndexOptions.indexOptions
import org.dizitart.no2.IndexType.NonUnique
import org.dizitart.no2.Nitrite
import java.security.SecureRandom
import java.util.*
import java.util.UUID.fromString

/**
 * A Nitrite implementation of the [ClientSecretRepository].
 *
 * - https://www.dizitart.org/nitrite-database/
 * - https://www.dizitart.org/nitrite-database/#potassium-nitrite
 */
class NitriteClientSecretRepository(database: Nitrite) : ClientSecretRepository {


    /**
     * Create a new instance of [NitriteClientSecretRepository] with an in-memory instance of [Nitrite]
     */
    constructor() : this(
        nitrite(userId = "client-secret", password = "tDZoA!izHO1wG#Zc*1fYvk%8b2wS^6f0")
    )

    private val repository = database.getRepository<ClientSecret> {

        createIndex(ClientSecret::clientId.name, indexOptions(NonUnique))

        val secureRandom = SecureRandom()
        fun generateSalt(length: Int = 16) = ByteArray(size = length).also(secureRandom::nextBytes)
        fun hash(secret: String) = OpenBSDBCrypt.generate(secret.toCharArray(),generateSalt(), 6)
        fun new(uuid: String, id: ClientId, secret: String) = ClientSecret(fromString(uuid), id, hash(secret))

        insert(new("a8d695e0-17e7-4da5-a422-9760faa47053", ConsumerZ, "7XLlyzjRpvICEkNrsgtOuuj1S30Bj9Xu"))
        insert(new("85558aa2-0a9a-459d-a3b7-2b0a0981a430", ConsumerZ, "ez5A9SwYrSHF2Z4Cxvd&amp;g9zMI1i*EGyn"))
        insert(new("01089a87-b7b8-4b69-8120-d110a70c5255", ConsumerX, "9VylF3DbEeJbtdbih3lqpNXBw@Non#bi"))
        insert(new("6e98adaa-eaa2-4acb-b83d-647e55acb0ca", ConsumerX, "DyMXE^BDIdQ3zRAyE2gQXKTP*xz0xCre"))
    }

    override fun insert(new: ClientSecret) {
        repository.insert(new)
    }

    override fun delete(id: UUID) {
        repository.remove(ClientSecret::id eq id)
    }

    override fun findById(id: UUID): ClientSecret? {
        return repository.find(ClientSecret::id eq id).firstOrDefault()
    }

    override fun findAllByClientId(clientId: ClientId): Set<ClientSecret> {
        return repository.find(ClientSecret::clientId eq clientId).toSet()
    }
}