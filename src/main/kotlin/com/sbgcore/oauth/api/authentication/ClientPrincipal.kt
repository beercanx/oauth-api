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
    override val id: ClientId,
    override val configuration: ClientConfiguration
) : ClientPrincipal() {

    constructor(configuration: ClientConfiguration) : this(configuration.id, configuration)

    init {
        require(configuration.type == ClientType.Confidential) { "type cannot be [${configuration.type}]" }
    }
}

data class PublicClient(
    override val id: ClientId,
    override val configuration: ClientConfiguration
) : ClientPrincipal() {

    constructor(configuration: ClientConfiguration) : this(configuration.id, configuration)

    init {
        require(configuration.type == ClientType.Public) { "type cannot be [${configuration.type}]" }
    }
}
