package uk.co.baconi.oauth.api.client

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.sequences.beEmpty
import io.kotest.matchers.sequences.shouldContain
import io.kotest.matchers.sequences.shouldHaveCount
import io.kotest.matchers.sequences.shouldNotContain
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.apache.commons.lang3.RandomStringUtils.random
import org.dizitart.no2.exceptions.UniqueConstraintException
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.api.client.ClientId.ConsumerX
import uk.co.baconi.oauth.api.client.ClientId.ConsumerY
import uk.co.baconi.oauth.api.enums.serialise
import java.util.UUID.randomUUID

class NitriteClientSecretRepositoryIntegrationTest {

    private val underTest = NitriteClientSecretRepository()

    @Nested
    inner class Insert {

        @Test
        fun `should create a new record`() {

            val clientSecret1 = ClientSecret(id = randomUUID(), clientId = ConsumerY, secret = "aardvark")
            underTest.insert(clientSecret1)

            val clientSecret2 = ClientSecret(id = randomUUID(), clientId = ConsumerY, secret = "badger")
            underTest.insert(clientSecret2)

            underTest.findById(clientSecret1.id) shouldBe clientSecret1
            underTest.findById(clientSecret2.id) shouldBe clientSecret2
        }

        @Test
        fun `should throw exception on inserting record with same uuid`() {

            val uuid = randomUUID()

            underTest.insert(ClientSecret(id = uuid, clientId = ConsumerY, secret = "aardvark"))

            shouldThrow<UniqueConstraintException> {
                underTest.insert(ClientSecret(id = uuid, clientId = ConsumerY, secret = "badger"))
            }
        }

        @Test
        fun `should throw exception on inserting record with same secret`() {

            underTest.insert(ClientSecret(id = randomUUID(), clientId = ConsumerY, secret = "aardvark"))

            shouldThrow<UniqueConstraintException> {
                underTest.insert(ClientSecret(id = randomUUID(), clientId = ConsumerY, secret = "aardvark"))
            }
        }
    }

    @Nested
    inner class Delete {

        @Test
        fun `should delete a record if it exists`() {

            val clientSecret = ClientSecret(id = randomUUID(), clientId = ConsumerY, secret = "aardvark")
            underTest.insert(clientSecret)
            underTest.findById(clientSecret.id) shouldBe clientSecret
            underTest.delete(clientSecret.id)
            underTest.findById(clientSecret.id) shouldBe null
        }

        @Test
        fun `should not throw exceptions when no record exists`() {
            underTest.delete(randomUUID())
        }
    }

    @Nested
    inner class FindById {

        @Test
        fun `should allow finding by uuid`() {

            (1..10).map {
                randomUUID()
            }.map { uuid ->
                ClientSecret(id = uuid, clientId = ConsumerY, secret = random(32))
            }.forEach { clientSecret ->
                underTest.insert(clientSecret)
                underTest.findById(clientSecret.id) shouldBe clientSecret
            }
        }

        @Test
        fun `should return null when no record exists`() {
            underTest.findById(randomUUID()) shouldBe null
        }
    }

    @Nested
    inner class FindAllByClientId {

        @Test
        fun `should return singleton collection when there's just one record`() {

            underTest.insert(ClientSecret(id = randomUUID(), clientId = ConsumerX, secret = random(32)))

            val consumerYClientSecret = ClientSecret(id = randomUUID(), clientId = ConsumerY, secret = random(32))
            underTest.insert(consumerYClientSecret)

            assertSoftly(underTest.findAllByClientId(ConsumerY)) {
                it shouldHaveCount 1
                it shouldContain consumerYClientSecret
            }
        }

        @Test
        fun `should return all secrets for the given client id`() {

            val consumerXClientSecret = ClientSecret(id = randomUUID(), clientId = ConsumerX, secret = random(32))
            underTest.insert(consumerXClientSecret)

            (1..10).map {
                randomUUID()
            }.map { uuid ->
                ClientSecret(id = uuid, clientId = ConsumerY, secret = random(32))
            }.forEach { clientSecret ->
                underTest.insert(clientSecret)
            }

            assertSoftly(underTest.findAllByClientId(ConsumerY)) {
                it shouldHaveCount 10
                it shouldNotContain consumerXClientSecret
            }
        }

        @Test
        fun `should return an empty sequence when no record exists`() {
            underTest.findAllByClientId(ConsumerY) should beEmpty()
        }
    }

    @Nested
    inner class FindAllByClientIdAsString {

        @Test
        fun `should return singleton collection when there's just one record`() {

            underTest.insert(ClientSecret(id = randomUUID(), clientId = ConsumerX, secret = random(32)))

            val clientSecret = ClientSecret(id = randomUUID(), clientId = ConsumerY, secret = "aardvark")
            underTest.insert(clientSecret)

            assertSoftly(underTest.findAllByClientId(ConsumerY.serialise())) {
                it shouldHaveCount 1
                it shouldContain clientSecret
            }
        }

        @Test
        fun `should return all secrets for the given client id`() {

            val consumerXClientSecret = ClientSecret(id = randomUUID(), clientId = ConsumerX, secret = random(32))
            underTest.insert(consumerXClientSecret)

            (1..10).map {
                randomUUID()
            }.map { uuid ->
                ClientSecret(id = uuid, clientId = ConsumerY, secret = random(32))
            }.forEach { clientSecret ->
                underTest.insert(clientSecret)
            }

            assertSoftly(underTest.findAllByClientId(ConsumerY.serialise())) {
                it shouldHaveCount 10
                it shouldNotContain consumerXClientSecret
            }
        }

        @Test
        fun `should return empty sequence when no record exists`() {
            underTest.findAllByClientId(ConsumerY) should beEmpty()
        }

        @Test
        fun `should return empty sequence when no client exists with that id`() {
            underTest.findAllByClientId("aardvark") should beEmpty()
        }
    }
}