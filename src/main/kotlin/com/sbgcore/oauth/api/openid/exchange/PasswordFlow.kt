package com.sbgcore.oauth.api.openid.exchange

import com.sbgcore.oauth.api.openid.AuthenticatedClient
import com.sbgcore.oauth.api.openid.Scopes

class PasswordFlow {

    fun exchange(client: AuthenticatedClient, scope: Set<Scopes>, username: String, password: String): Any {
        // TODO - Implement
        return "password flow tokens"
    }
}