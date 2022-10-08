package uk.co.baconi.oauth.api.common.client

import de.mkammerer.argon2.Argon2
import de.mkammerer.argon2.Argon2Factory
import de.mkammerer.argon2.Argon2Factory.Argon2Types.ARGON2id

class ClientSecretService internal constructor(
    private val clientSecretRepository: ClientSecretRepository,
    private val clientConfigurationRepository: ClientConfigurationRepository,
    private val checkPassword: (hash: String, password: CharArray) -> Boolean
) {

    constructor(
        clientSecretRepository: ClientSecretRepository,
        clientConfigurationRepository: ClientConfigurationRepository
    ): this(
        clientSecretRepository,
        clientConfigurationRepository,
        Argon2Factory.create(ARGON2id)
    )

    internal constructor(
        clientSecretRepository: ClientSecretRepository,
        clientConfigurationRepository: ClientConfigurationRepository,
        argon2: Argon2
    ) : this(
        clientSecretRepository,
        clientConfigurationRepository,
        checkPassword = { hash, password ->
            try {
                argon2.verify(hash, password)
            } finally {
                argon2.wipeArray(password)
            }
        }
    )

    /**
     * Authenticate a [ConfidentialClient].
     */
    fun authenticate(clientId: String, clientSecret: String): ConfidentialClient? {
        return clientSecretRepository
            .findAllByClientId(clientId)
            .filter { secret -> checkPassword(secret.hashedSecret, clientSecret.toCharArray()) }
            .map(ClientSecret::clientId)
            .mapNotNull(clientConfigurationRepository::findById)
            .filter(ClientConfiguration::isConfidential)
            .map(::ConfidentialClient)
            .firstOrNull()
    }

    /**
     * Authenticate a [PublicClient].
     */
    fun authenticate(clientId: String?): PublicClient? {
        return clientId
            ?.let(clientConfigurationRepository::findByClientId)
            ?.takeIf(ClientConfiguration::isPublic)
            ?.let(::PublicClient)
    }
}