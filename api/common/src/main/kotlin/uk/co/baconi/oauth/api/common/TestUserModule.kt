package uk.co.baconi.oauth.api.common

import de.mkammerer.argon2.Argon2Factory
import io.ktor.server.application.*
import uk.co.baconi.oauth.api.common.authentication.*

data class TestUser(val username: String, val password: String, val state: CustomerState)

@Deprecated("This is intended to be removed once code complete")
interface TestUserModule {

    val customerCredentialRepository: CustomerCredentialRepository
    val customerStatusRepository: CustomerStatusRepository

    @Deprecated("This is intended to be removed once code complete")
    fun Application.generateTestUsers(users: List<TestUser> = listOf(
        TestUser("aardvark","121212", CustomerState.Active),
        TestUser("badger", "212121", CustomerState.Active),
        TestUser("elephant", "122112", CustomerState.Active),

        TestUser("locked_000001", "000001", CustomerState.Locked),
        TestUser("locked_000002", "000002", CustomerState.Locked),

        TestUser("suspended_000001", "000001", CustomerState.Suspended),
        TestUser("suspended_000002", "000002", CustomerState.Suspended),

        TestUser("closed_000001", "000001", CustomerState.Closed),
        TestUser("closed_000002", "000002", CustomerState.Closed),
        TestUser("closed_000003", "000003", CustomerState.Closed),
    )) {

        log.info("Registering the TestUserModule.generateTestUsers() module")

        val argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id)

        users.forEach { (username, password, state) ->

            val customerCredential = CustomerCredential(
                username = username,
                hashedSecret = argon2.hash(2, 16, 1, password.toCharArray())
            ).also(customerCredentialRepository::insert)

            log.info("Generated: $customerCredential")

            val customerStatus = CustomerStatus(
                username = username,
                state = state
            ).also(customerStatusRepository::insert)

            log.info("Generated: $customerStatus")
        }
    }
}