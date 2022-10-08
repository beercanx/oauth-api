package uk.co.baconi.oauth.api.customer

import org.bouncycastle.crypto.generators.OpenBSDBCrypt
import org.dizitart.kno2.filters.eq
import org.dizitart.kno2.getRepository
import org.dizitart.kno2.nitrite
import org.dizitart.no2.Nitrite
import java.io.Closeable
import java.security.SecureRandom

class NitriteCustomerCredentialRepository(database: Nitrite) : CustomerCredentialRepository, Closeable by database {

    /**
     * Create a new instance of [NitriteCustomerCredentialRepository] with an in-memory instance of [Nitrite]
     */
    constructor() : this(
        nitrite(userId = "customer-credential", password = "B29Lwg24wcLD_5P2_LSu?6vcGAM+-Luc")
    )

    private val repository = database.getRepository<CustomerCredential> {

        // BCrypt support
        val secureRandom = SecureRandom()
        fun generateSalt(length: Int = 16) = ByteArray(size = length).also(secureRandom::nextBytes)
        fun hash(secret: String) = OpenBSDBCrypt.generate(secret.toCharArray(), generateSalt(), 6)

        // Add some initial test users
        insert(CustomerCredential(username = "AARDVARK", secret = hash("121212")))
        insert(CustomerCredential(username = "BADGER", secret = hash("212121")))
        insert(CustomerCredential(username = "ELEPHANT", secret = hash("122112")))
    }

    override fun insert(new: CustomerCredential) {
        repository.insert(new)
    }

    override fun delete(id: String) {
        repository.remove(CustomerCredential::username eq id)
    }

    override fun findById(id: String): CustomerCredential? {
        return repository.find(CustomerCredential::username eq id).firstOrDefault()
    }
}