package uk.co.baconi.oauth.api.common

import de.mkammerer.argon2.Argon2Factory
import io.ktor.server.application.*
import uk.co.baconi.oauth.api.common.authentication.*
import uk.co.baconi.oauth.common.authentication.*

@Deprecated("This is intended to be removed once code complete")
interface TestUserModule {

    val customerCredentialRepository: CustomerCredentialRepository
    val customerStatusRepository: CustomerStatusRepository

    @Deprecated("This is intended to be removed once code complete")
    fun Application.generateTestUsers() {

        log.info("Registering the TestUserModule.generateTestUsers() module")

        val argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id)

        val customerCredential = CustomerCredential(
            username = "aardvark",
            hashedSecret = argon2.hash(2, 16, 1, "121212".toCharArray())
        ).also(customerCredentialRepository::insert)

        log.info("Generated: $customerCredential")

        val customerStatus = CustomerStatus(
            username = "aardvark",
            state = CustomerState.Active
        ).also(customerStatusRepository::insert)

        log.info("Generated: $customerStatus")
    }
}