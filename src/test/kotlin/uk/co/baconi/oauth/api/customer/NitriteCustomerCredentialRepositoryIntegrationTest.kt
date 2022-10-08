package uk.co.baconi.oauth.api.customer

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.apache.commons.lang3.RandomStringUtils.random
import org.dizitart.kno2.nitrite
import org.dizitart.no2.exceptions.UniqueConstraintException
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class NitriteCustomerCredentialRepositoryIntegrationTest {

    private val underTest = NitriteCustomerCredentialRepository(
        nitrite(userId = "NitriteCustomerCredentialRepositoryIntegrationTest", password = random(32))
    )

    @Nested
    inner class Insert {

        @Test
        fun `should create a new record`() {

            val record1 = CustomerCredential(username = random(32), secret = "P@55w0rd")
            underTest.insert(record1)

            val record2 = CustomerCredential(username = random(32), secret = "P@55w0rd")
            underTest.insert(record2)

            underTest.findById(record1.username) shouldBe record1
            underTest.findById(record1.username) shouldNotBe record2

            underTest.findById(record2.username) shouldBe record2
            underTest.findById(record2.username) shouldNotBe record1
        }

        @Test
        fun `should throw exception on inserting record with same username`() {

            val username = random(32)

            underTest.insert(CustomerCredential(username = username, secret = "P@55w0rd"))

            shouldThrow<UniqueConstraintException> {
                underTest.insert(CustomerCredential(username = username, secret = "P@55w0rd"))
            }
        }
    }

    @Nested
    inner class Delete {

        @Test
        fun `should delete a record if it exists`() {

            val record = CustomerCredential(username = random(32), secret = "P@55w0rd")
            underTest.insert(record)
            underTest.findById(record.username) shouldBe record
            underTest.delete(record.username)
            underTest.findById(record.username) shouldBe null
        }

        @Test
        fun `should not throw exceptions when no record exists`() {
            underTest.delete(random(32))
        }
    }

    @Nested
    inner class FindById {

        @Test
        fun `should allow finding by username`() {

            (1..10).map {
                random(32)
            }.map { username ->
                CustomerCredential(username = username, secret = "P@55w0rd")
            }.forEach { record ->
                underTest.insert(record)
                underTest.findById(record.username) shouldBe record
            }
        }

        @Test
        fun `should return null when no record exists`() {
            underTest.findById(random(32)) shouldBe null
        }
    }

    @Nested
    inner class FindAllByUsername {

        @Test
        fun `should return record when it matches the username`() {

            val record = CustomerCredential(username = random(32), secret = "P@55w0rd")
            underTest.insert(record)
            underTest.findByUsername(record.username) shouldBe record
        }

        @Test
        fun `should return an null when no record exists`() {
            underTest.findById(random(32)) shouldBe null
        }
    }
}