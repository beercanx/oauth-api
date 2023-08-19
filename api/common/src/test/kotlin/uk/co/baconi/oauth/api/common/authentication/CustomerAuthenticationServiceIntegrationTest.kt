package uk.co.baconi.oauth.api.common.authentication

import de.mkammerer.argon2.Argon2Factory
import de.mkammerer.argon2.Argon2Factory.Argon2Types.ARGON2id
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.api.common.authentication.CustomerState.Active

class CustomerAuthenticationServiceIntegrationTest {

    companion object {

        private val database = Database.connect(
            url = "jdbc:h2:mem:CustomerAuthenticationServiceIntegrationTest;DB_CLOSE_DELAY=30;",
            driver = "org.h2.Driver"
        )

        private val customerCredentialRepository = CustomerCredentialRepository(database)
        private val customerStatusRepository = CustomerStatusRepository(database)

        init {
            transaction(database) {
                SchemaUtils.create(CustomerCredentialTable)
                SchemaUtils.create(CustomerStatusTable)

                val hash = Argon2Factory.create(ARGON2id).hash(2, 16, 1, "121212".toCharArray())

                customerCredentialRepository.insert(CustomerCredential("aardvark", hash))
                customerStatusRepository.insert(CustomerStatus("aardvark", Active))
            }
        }
    }

    private val underTest = CustomerAuthenticationService(customerCredentialRepository, customerStatusRepository)

    @Test
    fun `should be able to authenticate a valid customer`() {
        assertSoftly(underTest.authenticate("aardvark", "121212".toCharArray())) {
            shouldBeInstanceOf<CustomerAuthentication.Success>()
            username shouldBe AuthenticatedUsername("aardvark")
        }
    }
}