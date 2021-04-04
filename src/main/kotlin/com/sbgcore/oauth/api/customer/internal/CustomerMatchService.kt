package com.sbgcore.oauth.api.customer.internal

import com.sbgcore.oauth.api.customer.*
import org.bouncycastle.crypto.generators.OpenBSDBCrypt.checkPassword

class CustomerMatchService(
    private val customerCredentialRepository: CustomerCredentialRepository
) : MatchService {

    override suspend fun match(username: String, password: String): MatchResponse {

        val credential = customerCredentialRepository.findByUsername(username.toUpperCase())

        return when {

            // No credential
            credential == null -> MatchFailure

            // Credentials did not match
            !checkPassword(credential.secret, password.toCharArray()) -> {
                // TODO - Increment failure recorder
                MatchFailure
            }

            // Credential matched
            else -> MatchSuccess(
                username = credential.username, // TODO - Convert to typed object
            )
        }
    }
}