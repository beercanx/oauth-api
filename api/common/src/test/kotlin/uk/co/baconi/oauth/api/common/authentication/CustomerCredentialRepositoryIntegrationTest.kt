package uk.co.baconi.oauth.api.common.authentication

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import org.apache.commons.lang3.RandomStringUtils
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CustomerCredentialRepositoryIntegrationTest {

    companion object {

        private val database = Database.connect(
            url = "jdbc:h2:mem:CustomerCredentialRepositoryIntegrationTest;DB_CLOSE_DELAY=30;",
            driver = "org.h2.Driver"
        )

        init {
            transaction(database) {
                SchemaUtils.create(CustomerCredentialTable)
            }
        }
    }

    private val underTest = CustomerCredentialRepository(database)

    @Nested
    inner class Insert {

        @Test
        fun `should insert a new customer credential`() {
            underTest.insert(customerCredential())
        }

        @Test
        fun `should throw exception on duplicate customer credential`() {

            underTest.insert(customerCredential(username = "aardvark"))

            val exception = shouldThrow<ExposedSQLException> {
                underTest.insert(customerCredential(username = "aardvark"))
            }

            assertSoftly(exception.cause) {
                shouldBeInstanceOf<JdbcSQLIntegrityConstraintViolationException>()
                message shouldContain "Unique index or primary key violation"
            }
        }
    }

    @Nested
    inner class FindByUsername {

        @Test
        fun `should return a customer credential by its username`() {
            underTest.insert(customerCredential(username = "badger"))
            assertSoftly(underTest.findByUsername("badger")) {
                shouldNotBeNull()
                username shouldBe "badger"
            }
        }

        @Test
        fun `should return null when provided a username that does not exist`() {
            underTest.findByUsername("no-such-user").shouldBeNull()
        }
    }

    private fun customerCredential(
        username: String = RandomStringUtils.randomAlphanumeric(8),
        secret: String = "password"
    ): CustomerCredential {
        return CustomerCredential(
            username = username,
            hashedSecret = secret
        )
    }
}