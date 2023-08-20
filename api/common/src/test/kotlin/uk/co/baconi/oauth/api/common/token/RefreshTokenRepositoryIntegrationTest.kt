package uk.co.baconi.oauth.api.common.token

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.beEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.api.common.authentication.AuthenticatedUsername
import uk.co.baconi.oauth.api.common.client.ClientId
import uk.co.baconi.oauth.api.common.scope.Scope
import java.time.Instant
import java.time.temporal.ChronoUnit.HOURS
import java.util.*

/**
 * Integration testing using a H2 in memory database.
 */
class RefreshTokenRepositoryIntegrationTest {

    companion object {

        private val database = Database.connect(
            url = "jdbc:h2:mem:RefreshTokenRepositoryIntegrationTest;DB_CLOSE_DELAY=30;",
            driver = "org.h2.Driver"
        )

        init {
            transaction(database) {
                SchemaUtils.create(RefreshTokenTable)
            }
        }
    }

    private val underTest = RefreshTokenRepository(database)

    @Nested
    inner class Insert {

        @Test
        fun `should insert a new refresh token`() {
            underTest.insert(refreshToken())
        }

        @Test
        fun `should throw exception on duplicate refresh token`() {

            val uuid = UUID.randomUUID()

            underTest.insert(refreshToken(value = uuid))

            val exception = shouldThrow<ExposedSQLException> {
                underTest.insert(refreshToken(value = uuid))
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
        fun `should return an refresh token by its id`() {
            val uuid = UUID.randomUUID()
            val now = Instant.now()
            underTest.insert(refreshToken(value = uuid, now = now))
            underTest.findById(uuid) shouldBe refreshToken(value = uuid, now = now)
        }

        @Test
        fun `should return null when provided an id that does not exist`() {
            underTest.findById(UUID.randomUUID()).shouldBeNull()
        }
    }

    @Nested
    inner class FindAllByUsername {

        @Test
        fun `should return all the refresh tokens for a given username`() {
            underTest.insert(refreshToken(username = "find-all-by-username", clientId = "1"))
            underTest.insert(refreshToken(username = "find-all-by-username", clientId = "2"))
            underTest.insert(refreshToken(username = "find-all-by-username", clientId = "3"))
            underTest.insert(refreshToken(username = "find-all-by-username", clientId = "4"))
            underTest.insert(refreshToken(username = "find-all-by-username", clientId = "5"))

            underTest.findAllByUsername(AuthenticatedUsername("find-all-by-username")) shouldHaveSize 5
        }

        @Test
        fun `should return an empty sequence for a username with no refresh tokens`() {
            underTest.findAllByUsername(AuthenticatedUsername("find-none-by-username")) shouldHaveSize 0
        }
    }

    @Nested
    inner class FindAllByClientId {

        @Test
        fun `should return all the refresh tokens for a given client identifier`() {
            underTest.insert(refreshToken(username = "1", clientId = "find-all-by-client-id"))
            underTest.insert(refreshToken(username = "2", clientId = "find-all-by-client-id"))
            underTest.insert(refreshToken(username = "3", clientId = "find-all-by-client-id"))
            underTest.insert(refreshToken(username = "4", clientId = "find-all-by-client-id"))
            underTest.insert(refreshToken(username = "5", clientId = "find-all-by-client-id"))

            underTest.findAllByClientId(ClientId("find-all-by-client-id")) shouldHaveSize 5
        }

        @Test
        fun `should return an empty sequence for a client identifier with no refresh tokens`() {
            underTest.findAllByClientId(ClientId("find-none-by-client-id")) shouldHaveSize 0
        }
    }

    @Nested
    inner class DeleteById {

        @Test
        fun `should delete the refresh token by its identifier`() {
            val uuid = UUID.randomUUID()
            val now = Instant.now()
            underTest.insert(refreshToken(value = uuid, now = now))
            underTest.findById(uuid) shouldBe refreshToken(value = uuid, now = now)
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
        fun `should delete the refresh token`() {
            val refreshToken = refreshToken()
            underTest.insert(refreshToken)
            underTest.findById(refreshToken.value) shouldBe refreshToken
            underTest.deleteByRecord(refreshToken)
            underTest.findById(refreshToken.value).shouldBeNull()
        }

        @Test
        fun `should not fail if token does not exist`() {
            val refreshToken = refreshToken()
            underTest.findById(refreshToken.value).shouldBeNull()
            underTest.deleteByRecord(refreshToken)
            underTest.findById(refreshToken.value).shouldBeNull()
        }
    }

    @Nested
    inner class DeleteExpired {

        @Test
        fun `should delete any expired refresh tokens`() {
            val expired = refreshToken(now = Instant.now().minus(1, HOURS))
            underTest.insert(expired)
            underTest.findById(expired.value) shouldBe expired
            underTest.deleteExpired()
            underTest.findById(expired.value).shouldBeNull()
        }

        @Test
        fun `should not fail if no tokens are deleted`() {
            underTest.deleteExpired()
            underTest.findAllByUsername(AuthenticatedUsername("aardvark")) should beEmpty()
            underTest.findAllByClientId(ClientId("badger")) should beEmpty()
        }
    }

    private fun refreshToken(
        value: UUID = UUID.randomUUID(),
        username: String = "aardvark",
        clientId: String = "badger",
        scopes: Set<Scope> = setOf(Scope("basic"), Scope("profile::read"), Scope("profile::write")),
        now: Instant = Instant.now()
    ) = RefreshToken(
        value = value,
        username = AuthenticatedUsername(username),
        clientId = ClientId(clientId),
        scopes = scopes,
        issuedAt = now,
        expiresAt = now.plusSeconds(60),
        notBefore = now.minusSeconds(60)
    )
}