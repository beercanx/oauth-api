package uk.co.baconi.oauth.api.customer

import org.bouncycastle.crypto.generators.OpenBSDBCrypt

class CustomerMatchService internal constructor(
    private val customerCredentialRepository: CustomerCredentialRepository,
    private val checkPassword: (String, CharArray) -> Boolean
) {

    constructor(
        customerCredentialRepository: CustomerCredentialRepository
    ) : this(
        customerCredentialRepository,
        OpenBSDBCrypt::checkPassword
    )

    /**
     * Check if the provided username and password matches what we have stored.
     */
    fun match(username: String, password: String): CustomerMatchResponse {

        val credential = customerCredentialRepository.findByUsername(username.toUpperCase())

        return when {

            // No credential
            credential == null -> CustomerMatchFailure

            // Credentials did not match
            !checkPassword(credential.secret, password.toCharArray()) -> {
                // TODO - Increment failure recorder
                CustomerMatchFailure
            }

            // Credential matched
            else -> CustomerMatchSuccess(username = credential.username)
        }
    }
}