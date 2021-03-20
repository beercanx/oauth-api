package com.sbgcore.oauth.api.customer.internal

import org.bouncycastle.crypto.generators.OpenBSDBCrypt
import org.dizitart.kno2.filters.eq
import org.dizitart.kno2.getRepository
import org.dizitart.kno2.nitrite
import org.dizitart.no2.IndexOptions
import org.dizitart.no2.IndexType
import org.dizitart.no2.Nitrite
import java.security.SecureRandom

class NitriteInternalCredentialRepository(database: Nitrite) : InternalCredentialRepository {

    /**
     * Create a new instance of [NitriteInternalCredentialRepository] with an in-memory instance of [Nitrite]
     */
    constructor() : this(
        nitrite(userId = "internal-credential", password = "B29Lwg24wcLD_5P2_LSu?6vcGAM+-Luc")
    )

    private val repository = database.getRepository<InternalCredential> {

        // BCrypt support
        val secureRandom = SecureRandom()
        fun generateSalt(length: Int = 16) = ByteArray(size = length).also(secureRandom::nextBytes)
        fun hash(secret: String) = OpenBSDBCrypt.generate(secret.toCharArray(), generateSalt(), 6)

        // Add some initial test users
        insert(InternalCredential(username = "AARDVARK", secret = hash("121212")))
    }

    override fun insert(new: InternalCredential) {
        repository.insert(new)
    }

    override fun delete(id: String) {
        repository.remove(InternalCredential::username eq id)
    }

    override fun findById(id: String): InternalCredential? {
        return repository.find(InternalCredential::username eq id).firstOrDefault()
    }
}