package uk.co.baconi.oauth.api.common.client

class ClientAuthenticationService internal constructor(
    private val clientSecretRepository: ClientSecretRepository,
    private val clientConfigurationRepository: ClientConfigurationRepository,
    private val checkPassword: (ByteArray, String) -> Boolean
) {

    constructor(
        clientSecretRepository: ClientSecretRepository,
        clientConfigurationRepository: ClientConfigurationRepository
    ) : this(
        clientSecretRepository,
        clientConfigurationRepository,
        { _, _ -> false } // TODO - OpenBSDBCrypt::checkPassword
    )

    fun confidentialClient(clientId: String, clientSecret: String): ConfidentialClient? {
        return clientSecretRepository
            .findAllByClientId(clientId)
            .filter { secret -> checkPassword(secret.secret, clientSecret) }
            .map(ClientSecret::clientId)
            .mapNotNull(clientConfigurationRepository::findByClientId)
            .filter(ClientConfiguration::isConfidential)
            .map(::ConfidentialClient)
            .firstOrNull()
    }

    fun publicClient(clientId: String?): PublicClient? {
        return clientId
            ?.let(clientConfigurationRepository::findByClientId)
            ?.takeIf(ClientConfiguration::isPublic)
            ?.let(::PublicClient)
    }
}