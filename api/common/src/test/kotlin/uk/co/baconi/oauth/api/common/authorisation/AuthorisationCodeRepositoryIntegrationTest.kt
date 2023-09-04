package uk.co.baconi.oauth.api.common.authorisation

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldBeNull
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
import java.time.temporal.ChronoUnit
import java.util.*

class AuthorisationCodeRepositoryIntegrationTest {

    companion object {

        private val database = Database.connect(
            url = "jdbc:h2:mem:AuthorisationCodeRepositoryIntegrationTest;DB_CLOSE_DELAY=30;",
            driver = "org.h2.Driver"
        )

        init {
            transaction(database) {
                SchemaUtils.create(AuthorisationCodeTable)
            }
        }
    }

    private val underTest = AuthorisationCodeRepository(database)


    @Nested
    inner class Insert {

        @Nested
        inner class Basic {

            @Test
            fun `should insert a new access token`() {
                underTest.insert(authorisationCodeBasic())
            }

            @Test
            fun `should throw exception on duplicate access token`() {

                val uuid = UUID.randomUUID()

                underTest.insert(authorisationCodeBasic(value = uuid))

                val exception = shouldThrow<ExposedSQLException> {
                    underTest.insert(authorisationCodeBasic(value = uuid))
                }

                assertSoftly(exception.cause) {
                    shouldBeInstanceOf<JdbcSQLIntegrityConstraintViolationException>()
                    message shouldContain "Unique index or primary key violation"
                }
            }
        }

        @Nested
        inner class PKCE {

            @Test
            fun `should insert a new access token`() {
                underTest.insert(authorisationCodePkce())
            }

            @Test
            fun `should throw exception on duplicate access token`() {

                val uuid = UUID.randomUUID()

                underTest.insert(authorisationCodePkce(value = uuid))

                val exception = shouldThrow<ExposedSQLException> {
                    underTest.insert(authorisationCodePkce(value = uuid))
                }

                assertSoftly(exception.cause) {
                    shouldBeInstanceOf<JdbcSQLIntegrityConstraintViolationException>()
                    message shouldContain "Unique index or primary key violation"
                }
            }
        }
    }

    @Nested
    inner class FindById {

        @Test
        fun `should return null when provided an id that does not exist`() {
            underTest.findById(UUID.randomUUID()).shouldBeNull()
        }

        @Test
        fun `should return a basic authorisation code by its id`() {
            val uuid = UUID.randomUUID()
            val now = Instant.now()
            underTest.insert(authorisationCodeBasic(value = uuid, now = now))
            underTest.findById(uuid) shouldBe authorisationCodeBasic(value = uuid, now = now)
        }

        @Test
        fun `should return a pkce authorisation code by its id`() {
            val uuid = UUID.randomUUID()
            val now = Instant.now()
            underTest.insert(authorisationCodePkce(value = uuid, now = now))
            underTest.findById(uuid) shouldBe authorisationCodePkce(value = uuid, now = now)
        }
    }

    @Nested
    inner class DeleteById {

        @Test
        fun `should delete the authorisation code by its identifier`() {
            val uuid = UUID.randomUUID()
            val now = Instant.now()
            underTest.insert(authorisationCodeBasic(value = uuid, now = now))
            underTest.findById(uuid) shouldBe authorisationCodeBasic(value = uuid, now = now)
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
    inner class DeleteExpired {

        @Test
        fun `should delete any expired refresh tokens`() {
            val expired = authorisationCodeBasic(now = Instant.now().minus(1, ChronoUnit.HOURS))
            underTest.insert(expired)
            underTest.findById(expired.value) shouldBe expired
            underTest.deleteExpired()
            underTest.findById(expired.value).shouldBeNull()
        }

        @Test
        fun `should not fail if no tokens are deleted`() {
            underTest.deleteExpired()
        }
    }

    private fun authorisationCodeBasic(
        value: UUID = UUID.randomUUID(),
        username: String = "aardvark",
        clientId: String = "badger",
        redirectUri: String = "uk.co.baconi.oauth.consumerz://callback",
        scopes: Set<Scope> = setOf(Scope("basic"), Scope("profile::read"), Scope("profile::write")),
        now: Instant = Instant.now(),
        state: String = "da4d809e-ed89-42bd-aa3e-8c975b9242d0",
    ) = AuthorisationCode.Basic(
        value = value,
        username = AuthenticatedUsername(username),
        clientId = ClientId(clientId),
        scopes = scopes,
        redirectUri = redirectUri,
        issuedAt = now,
        expiresAt = now.plusSeconds(60),
        state = state,
    )

    private fun authorisationCodePkce(
        value: UUID = UUID.randomUUID(),
        username: String = "aardvark",
        clientId: String = "badger",
        redirectUri: String = "uk.co.baconi.oauth.consumerz://callback",
        scopes: Set<Scope> = setOf(Scope("basic"), Scope("profile::read"), Scope("profile::write")),
        now: Instant = Instant.now(),
        state: String = "da4d809e-ed89-42bd-aa3e-8c975b9242d0",
        codeChallenge: String = "code-challenge",
    ) = AuthorisationCode.PKCE(
        base = AuthorisationCode.Basic(
            value = value,
            username = AuthenticatedUsername(username),
            clientId = ClientId(clientId),
            scopes = scopes,
            redirectUri = redirectUri,
            issuedAt = now,
            expiresAt = now.plusSeconds(60),
            state = state
        ),
        codeChallenge = CodeChallenge(codeChallenge),
        codeChallengeMethod = CodeChallengeMethod.S256
    )
}
