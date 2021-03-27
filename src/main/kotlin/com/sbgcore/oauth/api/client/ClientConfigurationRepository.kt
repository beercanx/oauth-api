package com.sbgcore.oauth.api.client

import com.sbgcore.oauth.api.Repository

interface ClientConfigurationRepository : Repository<ClientConfiguration, ClientId> {
}