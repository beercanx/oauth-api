package uk.co.baconi.oauth.api.common.customer

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
import uk.co.baconi.oauth.common.authentication.CustomerState
import uk.co.baconi.oauth.common.authentication.CustomerStatus
import uk.co.baconi.oauth.common.authentication.CustomerStatusRepository
import uk.co.baconi.oauth.common.authentication.CustomerStatusTable

class CustomerStatusRepositoryIntegrationTest {

    companion object {

        private val database = Database.connect(
            url = "jdbc:h2:mem:CustomerStatusRepositoryIntegrationTest;DB_CLOSE_DELAY=30;",
            driver = "org.h2.Driver"
        )

        init {
            transaction(database) {
                SchemaUtils.create(CustomerStatusTable)
            }
        }
    }

    private val underTest = CustomerStatusRepository(database)

    @Nested
    inner class Insert {

        @Test
        fun `should insert a new customer status`() {
            underTest.insert(customerStatus())
        }

        @Test
        fun `should throw exception on duplicate customer status`() {

            underTest.insert(customerStatus(username = "aardvark"))

            val exception = shouldThrow<ExposedSQLException> {
                underTest.insert(customerStatus(username = "aardvark"))
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
        fun `should return a customer status by its username`() {
            underTest.insert(customerStatus(username = "badger"))
            assertSoftly(underTest.findByUsername("badger")) {
                shouldNotBeNull()
                username shouldBe "badger"
                state shouldBe CustomerState.Active
            }
        }

        @Test
        fun `should return null when provided a username that does not exist`() {
            underTest.findByUsername("no-such-user").shouldBeNull()
        }
    }

    private fun customerStatus(
        username: String = RandomStringUtils.randomAlphanumeric(8),
        state: CustomerState = CustomerState.Active
    ): CustomerStatus {
        return CustomerStatus(
            username = username,
            state = state
        )
    }
}