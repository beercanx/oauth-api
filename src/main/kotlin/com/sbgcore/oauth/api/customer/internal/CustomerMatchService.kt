package com.sbgcore.oauth.api.customer.internal

import com.sbgcore.oauth.api.customer.MatchFailureReason.Mismatch
import com.sbgcore.oauth.api.customer.MatchFailure
import com.sbgcore.oauth.api.customer.MatchResponse
import com.sbgcore.oauth.api.customer.MatchService
import com.sbgcore.oauth.api.customer.MatchSuccess
import org.bouncycastle.crypto.generators.OpenBSDBCrypt.checkPassword
import java.time.OffsetDateTime.now

class CustomerMatchService(
    private val customerCredentialRepository: CustomerCredentialRepository
) : MatchService {

    // TODO - Think how we will track match failures, lock an account, reset tracking of failures, enable unlocking.

    override suspend fun match(username: String, password: String): MatchResponse {

        val credential = customerCredentialRepository.findByUsername(username.toUpperCase())

        return if (credential != null && checkPassword(credential.secret, password.toCharArray())) {

            // TODO - Check for locked
            // TODO - Check for closed
            // TODO - Check for suspended

            MatchSuccess(
                customerId = -1L, // TODO - Get from some where
                username = credential.username,
                temporary = credential.temporary,
                lastLogin = now() // TODO - Store and return previous?
            )
        } else {
            MatchFailure(Mismatch)
        }
    }
}