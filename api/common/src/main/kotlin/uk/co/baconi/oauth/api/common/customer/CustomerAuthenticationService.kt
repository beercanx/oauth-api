package uk.co.baconi.oauth.api.common.customer

import org.bouncycastle.crypto.generators.OpenBSDBCrypt
import org.slf4j.LoggerFactory
import uk.co.baconi.oauth.api.common.authentication.AuthenticatedUsername
import uk.co.baconi.oauth.api.common.customer.CustomerAuthentication.Failure
import uk.co.baconi.oauth.api.common.customer.CustomerAuthentication.Failure.Reason
import uk.co.baconi.oauth.api.common.customer.CustomerAuthentication.Success

class CustomerAuthenticationService internal constructor(
    private val customerCredentialRepository: CustomerCredentialRepository,
    private val customerStatusRepository: CustomerStatusRepository,
    private val checkPassword: (String, CharArray) -> Boolean
) {

    constructor(
        customerCredentialRepository: CustomerCredentialRepository,
        customerStatusRepository: CustomerStatusRepository,
    ) : this(
        customerCredentialRepository,
        customerStatusRepository,
        OpenBSDBCrypt::checkPassword // TODO - Look to migrate to Argon2id as per OWASP recommendations
    )

    companion object {
        private val logger = LoggerFactory.getLogger(CustomerAuthenticationService::class.java)
    }

    fun authenticate(username: String, password: String): CustomerAuthentication {

        val credential = customerCredentialRepository.findByUsername(username)

        return when {

            // No credential
            credential == null -> Failure(Reason.Missing)

            // Credentials did not match
            !checkPassword(credential.hashedSecret, password.toCharArray()) -> Failure(Reason.Mismatched)

            // Credential matched
            else -> when (customerStatusRepository.findByUsername(username)?.state) {
                null -> {
                    logger.error("Unable to find customer status for $username")
                    Failure(Reason.Missing)
                }
                CustomerState.Closed -> Failure(Reason.Closed)
                CustomerState.Suspended -> Failure(Reason.Suspended)
                CustomerState.Locked -> Failure(Reason.Locked)
                // TODO - What about change password?
                CustomerState.Active -> Success(AuthenticatedUsername(username))
            }
        }
    }

}