package com.sbgcore.oauth.api.client

import com.sbgcore.oauth.api.Repository
import com.sbgcore.oauth.api.enums.enumByValue

interface ClientConfigurationRepository : Repository<ClientConfiguration, ClientId> {
    fun findByClientId(clientId: ClientId): ClientConfiguration? = findById(clientId)
    fun findByClientId(clientId: String): ClientConfiguration? = enumByValue<ClientId>(clientId)?.let(::findById)
}