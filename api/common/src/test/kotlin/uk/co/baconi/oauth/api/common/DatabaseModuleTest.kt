package uk.co.baconi.oauth.api.common

import com.typesafe.config.Config
import com.typesafe.config.ConfigException
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.api.common.authentication.*
import uk.co.baconi.oauth.api.common.authentication.CustomerState.Active
import uk.co.baconi.oauth.api.common.authorisation.AuthorisationCode
import uk.co.baconi.oauth.api.common.authorisation.AuthorisationCodeRepository
import uk.co.baconi.oauth.api.common.client.ClientId
import uk.co.baconi.oauth.api.common.token.AccessToken
import uk.co.baconi.oauth.api.common.token.AccessTokenRepository
import uk.co.baconi.oauth.api.common.token.RefreshToken
import uk.co.baconi.oauth.api.common.token.RefreshTokenRepository
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

class DatabaseModuleTest : DatabaseModule {

    override val databaseConfiguration: Config = mockk {
        val path = slot<String>()
        every { getString(capture(path)) } answers {
            val (db, property) = path.captured.split('.', limit = 2)
            when (property) {
                "url" -> "jdbc:h2:mem:DatabaseModuleTest-$db;DB_CLOSE_DELAY=60;USER=aardvark;PASSWORD=badger"
                "driver" -> "org.h2.Driver"
                "user" -> "aardvark"
                "password" -> "badger"
                else -> throw ConfigException.Missing(property)
            }
        }
    }

    @Nested
    inner class AccessTokenDatabase {

        init {
            verify(exactly = 0) { databaseConfiguration.getString("access-token.url") }
        }

        private val accessTokenRepository = AccessTokenRepository(accessTokenDatabase)

        init {
            verify { databaseConfiguration.getString("access-token.url") }
        }

        @Test
        fun `should be able to provide an access token database when required`() {
            accessTokenRepository.insert(
                AccessToken(
                    value = UUID.randomUUID(),
                    username = AuthenticatedUsername("aardvark"),
                    clientId = ClientId("consumer-z"),
                    scopes = emptySet(),
                    issuedAt = Instant.now(),
                    expiresAt = Instant.now().minus(1, ChronoUnit.DAYS),
                    notBefore = Instant.now()
                )
            )
        }
    }

    @Nested
    inner class RefreshTokenDatabase {

        init {
            verify(exactly = 0) { databaseConfiguration.getString("refresh-token.url") }
        }

        private val refreshTokenRepository = RefreshTokenRepository(refreshTokenDatabase)

        init {
            verify { databaseConfiguration.getString("refresh-token.url") }
        }

        @Test
        fun `should be able to provide a refresh token database when required`() {
            refreshTokenRepository.insert(
                RefreshToken(
                    value = UUID.randomUUID(),
                    username = AuthenticatedUsername("aardvark"),
                    clientId = ClientId("consumer-z"),
                    scopes = emptySet(),
                    issuedAt = Instant.now(),
                    expiresAt = Instant.now().minus(1, ChronoUnit.DAYS),
                    notBefore = Instant.now()
                )
            )
        }
    }

    @Nested
    inner class AuthorisationCodeDatabase {

        init {
            verify(exactly = 0) { databaseConfiguration.getString("authorisation-code.url") }
        }

        private val authorisationCodeRepository = AuthorisationCodeRepository(authorisationCodeDatabase)

        init {
            verify { databaseConfiguration.getString("authorisation-code.url") }
        }

        @Test
        fun `should be able to provide an authorisation code database when required`() {
            authorisationCodeRepository.insert(
                AuthorisationCode.Basic(
                    value = UUID.randomUUID(),
                    username = AuthenticatedUsername("aardvark"),
                    clientId = ClientId("consumer-z"),
                    scopes = emptySet(),
                    redirectUri = "uk.co.baconi.oauth.consumerz://callback",
                    issuedAt = Instant.now(),
                    expiresAt = Instant.now().minus(1, ChronoUnit.DAYS),
                    state = UUID.randomUUID().toString(),
                )
            )
        }
    }

    @Nested
    inner class CustomerStatusDatabase {

        init {
            verify(exactly = 0) { databaseConfiguration.getString("customer-status.url") }
        }

        private val customerStatusRepository = CustomerStatusRepository(customerStatusDatabase)

        init {
            verify { databaseConfiguration.getString("customer-status.url") }
        }

        @Test
        fun `should be able to provide an customer status database when required`() {
            customerStatusRepository.insert(CustomerStatus("aardvark", Active))
        }
    }

    @Nested
    inner class CustomerCredentialDatabase {

        init {
            verify(exactly = 0) { databaseConfiguration.getString("customer-credential.url") }
        }

        private val customerCredentialRepository = CustomerCredentialRepository(customerCredentialDatabase)

        init {
            verify { databaseConfiguration.getString("customer-credential.url") }
        }

        @Test
        fun `should be able to provide an customer credential database when required`() {
            customerCredentialRepository.insert(CustomerCredential("aardvark", "Badger"))
        }
    }
}