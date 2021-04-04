package com.sbgcore.oauth.api.customer.internal

import com.sbgcore.oauth.api.customer.*
import com.sbgcore.oauth.api.customer.MatchFailureReason.*
import com.sbgcore.oauth.api.customer.internal.CustomerState.Active
import org.bouncycastle.crypto.generators.OpenBSDBCrypt.checkPassword
import org.slf4j.LoggerFactory
import java.time.OffsetDateTime.now

class CustomerMatchService(
    private val customerCredentialRepository: CustomerCredentialRepository,
    private val customerStatusRepository: CustomerStatusRepository,
) : MatchService {

    companion object {
        private val logger = LoggerFactory.getLogger(CustomerMatchService::class.java)
    }

    // TODO - Think how we will track match failures, lock an account, reset tracking of failures, enable unlocking.
    override suspend fun match(username: String, password: String): MatchResponse {

        val credential = customerCredentialRepository.findByUsername(username.toUpperCase())

        // TODO - Extract customer status checks to another layer, matching a customer should not matter
        val status = credential?.username?.let(customerStatusRepository::findByUsername)

        return when {

            // No credential
            credential == null -> MatchFailure(Mismatch)

            // Credentials did not match
            !checkPassword(credential.secret, password.toCharArray()) -> {

                // TODO - Track failures, count, ledger/log?
                // TODO - Lock?

                MatchFailure(Mismatch)
            }

            // Well dam, we've no status for this user, so its not safe to let them in.
            status == null -> {
                logger.error("Failed to find customers status record for [{}].", credential.username)
                MatchFailure(Mismatch)
            }

            // Matched account is locked
            isLocked(status) -> MatchFailure(Locked)

            // Must have an active state to proceed
            status.state != Active -> MatchFailure(Mismatch)

            // Credential matched and active
            else -> {

                // TODO - Reset failure counter?

                MatchSuccess(
                    customerId = -1L, // TODO - Get from some where? or remove it from the design.
                    username = credential.username,
                    temporary = status.changePassword, // TODO - Move to returning status and implementing "pre logged in actions"
                    lastLogin = now() // TODO - Store and return previous? Migrate to the status logic and out of a match
                )
            }
        }
    }

    private fun isLocked(status: CustomerStatus): Boolean {

        // TODO - Unlock? Say if enough time has passed since the last failure
        // TODO - Convert isLocked into a concept backed by events rather than a static state?

        return status.isLocked
    }
}