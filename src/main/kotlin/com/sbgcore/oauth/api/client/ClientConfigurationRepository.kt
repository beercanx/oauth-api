package com.sbgcore.oauth.api.client

import com.sbgcore.oauth.api.Repository
import com.sbgcore.oauth.api.enums.enumByJson

interface ClientConfigurationRepository : Repository<ClientConfiguration, ClientId> {
    fun findByClientId(clientId: ClientId): ClientConfiguration? = findById(clientId)
    fun findByClientId(clientId: String): ClientConfiguration? = enumByJson<ClientId>(clientId)?.let(::findById)
}