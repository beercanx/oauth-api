package com.sbgcore.oauth.api.openid.exchange

import com.sbgcore.oauth.api.openid.AuthenticatedClient

class PasswordFlow {

    fun exchange(client: AuthenticatedClient, scope: String, username: String, password: String): Any {
        // TODO - Implement
        return "password flow tokens"
    }
}