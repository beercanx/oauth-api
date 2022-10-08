package uk.co.baconi.oauth.api.tokens

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import org.apache.commons.lang3.RandomStringUtils.random
import org.dizitart.no2.exceptions.UniqueConstraintException
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.api.client.ClientId
import uk.co.baconi.oauth.api.client.ClientId.*
import uk.co.baconi.oauth.api.openid.Scopes.OpenId
import java.time.OffsetDateTime.now
import java.util.*
import java.util.UUID.randomUUID

class NitriteAccessTokenRepositoryIntegrationTest {

    private val underTest = NitriteAccessTokenRepository()

    private fun AccessToken.Companion.new(
        id: UUID = randomUUID(),
        value: UUID = randomUUID(),
        username: String = random(8),
        clientId: ClientId = ConsumerZ
    ) = AccessToken(
        id = id,
        value = value,
        username = username,
        clientId = clientId,
        scopes = setOf(OpenId),
        issuedAt = now(),
        expiresAt = now().plusDays(1),
        notBefore = now().minusDays(1)
    )

    @Nested
    inner class Insert {

        @Test
        fun `should create a new record`() {

            val accessToken1 = AccessToken.new(username = "aardvark").also(underTest::insert)
            val accessToken2 = AccessToken.new(username = "badger").also(underTest::insert)

            assertSoftly {

                underTest.findById(accessToken1.id) shouldBe accessToken1
                underTest.findById(accessToken1.id) shouldNotBeSameInstanceAs accessToken1

                underTest.findById(accessToken2.id) shouldBe accessToken2
                underTest.findById(accessToken2.id) shouldNotBeSameInstanceAs accessToken2
            }
        }

        @Test
        fun `should allow multiple records with the same username`() {

            val accessToken1 = AccessToken.new(username = "aardvark").also(underTest::insert)
            val accessToken2 = AccessToken.new(username = "aardvark").also(underTest::insert)

            assertSoftly {

                accessToken1 shouldNotBe accessToken2
                accessToken1 shouldNotBeSameInstanceAs accessToken2

                underTest.findById(accessToken1.id) shouldBe accessToken1
                underTest.findById(accessToken2.id) shouldBe accessToken2
            }
        }

        @Test
        fun `should throw exception on inserting record with same token id`() {

            val uuid = randomUUID()

            AccessToken.new(id = uuid).also(underTest::insert)

            shouldThrow<UniqueConstraintException> {
                AccessToken.new(id = uuid).also(underTest::insert)
            }
        }

        @Test
        fun `should throw exception on inserting record with same token value`() {

            val uuid = randomUUID()

            AccessToken.new(value = uuid).also(underTest::insert)

            shouldThrow<UniqueConstraintException> {
                AccessToken.new(value = uuid).also(underTest::insert)
            }
        }
    }

    @Nested
    inner class Delete {

        @Test
        fun `should delete a record if it exists`() {

            val accessToken = AccessToken.new()
            underTest.insert(accessToken)
            underTest.findById(accessToken.id) shouldBe accessToken
            underTest.delete(accessToken.id)
            underTest.findById(accessToken.id) shouldBe null
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
                AccessToken.new(id = uuid)
            }.forEach { accessToken ->
                underTest.insert(accessToken)
                underTest.findById(accessToken.id) shouldBe accessToken
            }
        }

        @Test
        fun `should return null when no record exists`() {
            underTest.findById(randomUUID()) shouldBe null
        }
    }

    @Nested
    inner class FindByValue {

        @Test
        fun `should allow finding by uuid value`() {

            (1..10).map {
                randomUUID()
            }.map { uuid ->
                uuid to AccessToken.new(value = uuid)
            }.forEach { (uuid, accessToken) ->
                underTest.insert(accessToken)
                underTest.findByValue(uuid.toString()) shouldBe accessToken
            }
        }

        @Test
        fun `should return null when no record exists`() {
            underTest.findByValue(randomUUID().toString()) shouldBe null
        }
    }

    @Nested
    inner class FindAllByClientId {

        @Test
        fun `should return singleton collection when there's just one record`() {

            underTest.insert(AccessToken.new())

            val accessToken = AccessToken.new(clientId = ConsumerY)
            underTest.insert(accessToken)

            assertSoftly(underTest.findAllByClientId(ConsumerY)) {
                it shouldHaveSize 1
                it shouldContain accessToken
            }
        }

        @Test
        fun `should return all secrets for the given client id`() {

            val consumerXAccessToken = AccessToken.new(clientId = ConsumerX)
            underTest.insert(consumerXAccessToken)

            (1..10).map {
                AccessToken.new(clientId = ConsumerY)
            }.forEach { accessToken ->
                underTest.insert(accessToken)
            }

            assertSoftly(underTest.findAllByClientId(ConsumerY)) {
                it shouldHaveSize 10
                it shouldNotContain consumerXAccessToken
            }
        }

        @Test
        fun `should return an empty sequence when no record exists`() {
            underTest.findAllByClientId(ConsumerY).shouldBeEmpty()
        }
    }

    @Nested
    inner class FindAllByUsername {

        @Test
        fun `should return singleton collection when there's just one record`() {

            val accessToken = AccessToken.new()
            underTest.insert(accessToken)

            assertSoftly(underTest.findAllByUsername(accessToken.username)) {
                it shouldHaveSize 1
                it shouldContain accessToken
            }
        }

        @Test
        fun `should return all access tokens for the given username`() {

            val accessToken = AccessToken.new()
            underTest.insert(accessToken)

            (1..10).map {
                AccessToken.new(username = "mashed potatoes")
            }.forEach { clientSecret ->
                underTest.insert(clientSecret)
            }

            assertSoftly(underTest.findAllByUsername("mashed potatoes")) {
                it shouldHaveSize 10
                it shouldNotContain accessToken
            }
        }

        @Test
        fun `should return an empty sequence when no record exists with username`() {
            underTest.findAllByUsername(random(32)).shouldBeEmpty()
        }
    }
}