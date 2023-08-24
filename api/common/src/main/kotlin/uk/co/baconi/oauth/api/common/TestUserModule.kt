package uk.co.baconi.oauth.api.common

import de.mkammerer.argon2.Argon2Factory
import io.ktor.server.application.*
import uk.co.baconi.oauth.api.common.authentication.*

@Deprecated("This is intended to be removed once code complete")
interface TestUserModule {

    val customerCredentialRepository: CustomerCredentialRepository
    val customerStatusRepository: CustomerStatusRepository

    @Deprecated("This is intended to be removed once code complete")
    fun Application.generateTestUsers(users: Map<String, String> = mapOf(
        "aardvark" to "121212",
        "badger" to "212121",
        "elephant" to "122112",
    )) {

        log.info("Registering the TestUserModule.generateTestUsers() module")

        val argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id)

        users.forEach { (username, password) ->

            val customerCredential = CustomerCredential(
                username = username,
                hashedSecret = argon2.hash(2, 16, 1, password.toCharArray())
            ).also(customerCredentialRepository::insert)

            log.info("Generated: $customerCredential")

            val customerStatus = CustomerStatus(
                username = username,
                state = CustomerState.Active
            ).also(customerStatusRepository::insert)

            log.info("Generated: $customerStatus")
        }
    }
}