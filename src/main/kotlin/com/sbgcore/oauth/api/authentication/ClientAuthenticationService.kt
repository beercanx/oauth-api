package com.sbgcore.oauth.api.authentication

import com.sbgcore.oauth.api.client.ClientConfiguration
import com.sbgcore.oauth.api.client.ClientConfigurationRepository
import org.bouncycastle.crypto.generators.OpenBSDBCrypt

class ClientAuthenticationService(
    private val clientSecretRepository: ClientSecretRepository,
    private val clientConfigurationRepository: ClientConfigurationRepository
) {

    fun confidentialClient(clientId: String, clientSecret: String): ConfidentialClient? {
        return clientSecretRepository
            .findAllByClientId(clientId)
            .filter { secret -> OpenBSDBCrypt.checkPassword(secret.secret, clientSecret.toCharArray()) }
            .map(ClientSecret::clientId)
            .mapNotNull(clientConfigurationRepository::findByClientId)
            .filter(ClientConfiguration::isConfidential)
            .map(::ConfidentialClient)
            .firstOrNull()
    }

    fun publicClient(clientId: String?) : PublicClient? {
        return clientId
            ?.let(clientConfigurationRepository::findByClientId)
            ?.takeIf(ClientConfiguration::isPublic)
            ?.let(::PublicClient)
    }
}