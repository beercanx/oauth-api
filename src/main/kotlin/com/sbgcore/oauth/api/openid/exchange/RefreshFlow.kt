package com.sbgcore.oauth.api.openid.exchange

import com.sbgcore.oauth.api.openid.AuthenticatedClient
import com.sbgcore.oauth.api.openid.Scopes

class RefreshFlow {

    fun exchange(client: AuthenticatedClient, refreshToken: String, scope: Set<Scopes>): Any {
        // TODO - Implement
        return "refresh flow tokens"
    }
}