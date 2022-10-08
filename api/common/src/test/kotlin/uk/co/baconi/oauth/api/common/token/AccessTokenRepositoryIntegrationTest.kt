package uk.co.baconi.oauth.api.common.token

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.beInstanceOf
import io.kotest.matchers.types.shouldBeInstanceOf
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.common.authentication.AuthenticatedUsername
import uk.co.baconi.oauth.api.common.client.ClientId
import uk.co.baconi.oauth.api.common.scope.Scope
import java.time.Instant
import java.util.*

/**
 * Integration testing using a H2 in memory database.
 */
class AccessTokenRepositoryIntegrationTest {

    companion object {

        private val database = Database.connect(
            url = "jdbc:h2:mem:AccessTokenRepositoryIntegrationTest;DB_CLOSE_DELAY=30;",
            driver = "org.h2.Driver"
        )

        init {
            transaction(database) {
                SchemaUtils.create(AccessTokenTable)
            }
        }
    }

    private val underTest = AccessTokenRepository(database)

    @Nested
    inner class Insert {

        @Test
        fun `should insert a new access token`() {
            underTest.insert(accessToken())
        }

        @Test
        fun `should throw exception on duplicate access token`() {

            val uuid = UUID.randomUUID()

            underTest.insert(accessToken(value = uuid))

            val exception = shouldThrow<ExposedSQLException> {
                underTest.insert(accessToken(value = uuid))
            }

            assertSoftly(exception.cause) {
                shouldBeInstanceOf<JdbcSQLIntegrityConstraintViolationException>()
                message shouldContain "Unique index or primary key violation"
            }
        }
    }

    @Nested
    inner class FindById {

        @Test
        fun `should return an access token by its id`() {
            val uuid = UUID.randomUUID()
            val now = Instant.now()
            underTest.insert(accessToken(value = uuid, now = now))
            underTest.findById(uuid) shouldBe accessToken(value = uuid, now = now)
        }

        @Test
        fun `should return null when provided an id that does not exist`() {
            underTest.findById(UUID.randomUUID()).shouldBeNull()
        }
    }

    @Nested
    inner class FindAllByUsername {

        @Test
        fun `should return all the access tokens for a given username`() {
            underTest.insert(accessToken(username = "find-all-by-username", clientId = "1"))
            underTest.insert(accessToken(username = "find-all-by-username", clientId = "2"))
            underTest.insert(accessToken(username = "find-all-by-username", clientId = "3"))
            underTest.insert(accessToken(username = "find-all-by-username", clientId = "4"))
            underTest.insert(accessToken(username = "find-all-by-username", clientId = "5"))

            underTest.findAllByUsername("find-all-by-username") shouldHaveSize 5
        }

        @Test
        fun `should return an empty sequence for a username with no access tokens`() {
            underTest.findAllByUsername("find-none-by-username") shouldHaveSize 0
        }
    }

    @Nested
    inner class FindAllByClientId {

        @Test
        fun `should return all the access tokens for a given client identifier`() {
            underTest.insert(accessToken(username = "1", clientId = "find-all-by-client-id"))
            underTest.insert(accessToken(username = "2", clientId = "find-all-by-client-id"))
            underTest.insert(accessToken(username = "3", clientId = "find-all-by-client-id"))
            underTest.insert(accessToken(username = "4", clientId = "find-all-by-client-id"))
            underTest.insert(accessToken(username = "5", clientId = "find-all-by-client-id"))

            underTest.findAllByClientId(ClientId("find-all-by-client-id")) shouldHaveSize 5
        }

        @Test
        fun `should return an empty sequence for a client identifier with no access tokens`() {
            underTest.findAllByClientId(ClientId("find-none-by-client-id")) shouldHaveSize 0
        }
    }

    @Nested
    inner class DeleteById {

        @Test
        fun `should delete the access token by its identifier`() {
            val uuid = UUID.randomUUID()
            val now = Instant.now()
            underTest.insert(accessToken(value = uuid, now = now))
            underTest.findById(uuid) shouldBe accessToken(value = uuid, now = now)
            underTest.deleteById(uuid)
            underTest.findById(uuid).shouldBeNull()
        }

        @Test
        fun `should not fail if token does not exist by the given identifier`() {
            val uuid = UUID.randomUUID()
            underTest.findById(uuid).shouldBeNull()
            underTest.deleteById(uuid)
            underTest.findById(uuid).shouldBeNull()
        }
    }

    @Nested
    inner class DeleteByRecord {

        @Test
        fun `should delete the access token`() {
            val accessToken = accessToken()
            underTest.insert(accessToken)
            underTest.findById(accessToken.value) shouldBe accessToken
            underTest.deleteByRecord(accessToken)
            underTest.findById(accessToken.value).shouldBeNull()
        }

        @Test
        fun `should not fail if token does not exist`() {
            val accessToken = accessToken()
            underTest.findById(accessToken.value).shouldBeNull()
            underTest.deleteByRecord(accessToken)
            underTest.findById(accessToken.value).shouldBeNull()
        }
    }

    private fun accessToken(
        value: UUID = UUID.randomUUID(),
        username: String = "aardvark",
        clientId: String = "badger",
        scopes: Set<Scope> = setOf(Scope.OpenId, Scope.ProfileRead, Scope.ProfileWrite),
        now: Instant = Instant.now()
    ) = AccessToken(
        value = value,
        username = AuthenticatedUsername(username),
        clientId = ClientId(clientId),
        scopes = scopes,
        issuedAt = now,
        expiresAt = now.plusSeconds(60),
        notBefore = now.minusSeconds(60)
    )
}