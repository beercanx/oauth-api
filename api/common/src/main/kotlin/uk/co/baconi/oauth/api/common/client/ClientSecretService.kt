package uk.co.baconi.oauth.api.common.client

import org.bouncycastle.crypto.generators.OpenBSDBCrypt

class ClientSecretService internal constructor(
    private val clientSecretRepository: ClientSecretRepository,
    private val clientConfigurationRepository: ClientConfigurationRepository,
    private val checkPassword: (hash: String, password: CharArray) -> Boolean
) {

    constructor(
        clientSecretRepository: ClientSecretRepository,
        clientConfigurationRepository: ClientConfigurationRepository
    ) : this(
        clientSecretRepository,
        clientConfigurationRepository,
        OpenBSDBCrypt::checkPassword  // TODO - Look to migrate to Argon2id as per OWASP recommendations
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