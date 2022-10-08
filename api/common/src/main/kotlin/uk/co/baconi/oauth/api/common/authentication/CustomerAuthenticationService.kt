package uk.co.baconi.oauth.api.common.authentication

import de.mkammerer.argon2.Argon2
import de.mkammerer.argon2.Argon2Factory
import de.mkammerer.argon2.Argon2Factory.Argon2Types.ARGON2id
import org.slf4j.LoggerFactory
import uk.co.baconi.oauth.api.common.authentication.CustomerAuthentication.Success
import uk.co.baconi.oauth.api.common.authentication.CustomerAuthentication.Failure
import uk.co.baconi.oauth.api.common.authentication.CustomerAuthentication.Failure.*

class CustomerAuthenticationService internal constructor(
    private val customerCredentialRepository: CustomerCredentialRepository,
    private val customerStatusRepository: CustomerStatusRepository,
    private val checkPassword: (String, CharArray) -> Boolean
) {

    constructor(
        customerCredentialRepository: CustomerCredentialRepository,
        customerStatusRepository: CustomerStatusRepository,
        argon2: Argon2 = Argon2Factory.create(ARGON2id) // TODO - Setup better?
    ) : this(
        customerCredentialRepository,
        customerStatusRepository,
        checkPassword = { hash, password ->
            try {
                argon2.verify(hash, password)
            } finally {
                argon2.wipeArray(password)
            }
        }
    )

    companion object {
        private val logger = LoggerFactory.getLogger(CustomerAuthenticationService::class.java)
    }

    fun authenticate(username: String, password: CharArray): CustomerAuthentication {

        val credential = customerCredentialRepository.findByUsername(username)

        return when {

            // No stored credential check is skipped to reduce surface area of a time-based attack.
            // credential == null -> Failure(Reason.Missing)

            // Credentials did not match
            !checkPassword(credential?.hashedSecret ?: "", password) -> Failure(Reason.Mismatched)

            // Credential matched
            else -> when (customerStatusRepository.findByUsername(username)?.state) {
                null -> {
                    logger.error("Unable to find customer status for $username")
                    Failure(Reason.Missing)
                }
                CustomerState.Closed -> Failure(Reason.Closed)
                CustomerState.Suspended -> Failure(Reason.Suspended)
                CustomerState.Locked -> Failure(Reason.Locked)
                CustomerState.Active -> Success(AuthenticatedUsername(username))
            }
        }
    }
}