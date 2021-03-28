package com.sbgcore.oauth.api.authentication

import com.sbgcore.oauth.api.client.ClientConfiguration
import com.sbgcore.oauth.api.client.ClientId
import com.sbgcore.oauth.api.client.ClientType
import io.ktor.auth.*

sealed class ClientPrincipal : Principal {
    abstract val id: ClientId
    abstract val configuration: ClientConfiguration
}

data class ConfidentialClient(
    override val configuration: ClientConfiguration
) : ClientPrincipal() {

    override val id: ClientId = configuration.id

    init {
        require(configuration.type == ClientType.Confidential) { "type cannot be [${configuration.type}]" }
    }
}

data class PublicClient(
    override val configuration: ClientConfiguration
) : ClientPrincipal() {

    override val id: ClientId = configuration.id

    init {
        require(configuration.type == ClientType.Public) { "type cannot be [${configuration.type}]" }
    }
}
