package uk.co.baconi.oauth.api.common.client

import uk.co.baconi.oauth.api.common.crypto.CheckHashedPassword
import uk.co.baconi.oauth.api.common.crypto.CheckPassword
import kotlin.text.toCharArray

class ClientAuthenticationService internal constructor(
    private val clientSecretRepository: ClientSecretRepository,
    private val clientConfigurationRepository: ClientConfigurationRepository,
    private val checkPassword: CheckHashedPassword
) {

    constructor(
        clientSecretRepository: ClientSecretRepository,
        clientConfigurationRepository: ClientConfigurationRepository
    ) : this(
        clientSecretRepository,
        clientConfigurationRepository,
        CheckPassword.checkHashedPassword
    )

    fun confidentialClient(clientId: String, clientSecret: String): ConfidentialClient? {
        return clientSecretRepository
            .findAllByClientId(clientId)
            .filter { secret -> checkPassword(secret.secret, clientSecret.toCharArray()) }
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