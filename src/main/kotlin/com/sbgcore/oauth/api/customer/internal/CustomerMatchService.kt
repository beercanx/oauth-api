package com.sbgcore.oauth.api.customer.internal

import com.sbgcore.oauth.api.customer.MatchFailure
import com.sbgcore.oauth.api.customer.MatchResponse
import com.sbgcore.oauth.api.customer.MatchService
import com.sbgcore.oauth.api.customer.MatchSuccess
import org.bouncycastle.crypto.generators.OpenBSDBCrypt

class CustomerMatchService internal constructor(
    private val customerCredentialRepository: CustomerCredentialRepository,
    private val checkPassword: (String, CharArray) -> Boolean
) : MatchService {

    constructor(
        customerCredentialRepository: CustomerCredentialRepository
    ) : this(
        customerCredentialRepository,
        OpenBSDBCrypt::checkPassword
    )

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
            else -> MatchSuccess(username = credential.username)
        }
    }
}