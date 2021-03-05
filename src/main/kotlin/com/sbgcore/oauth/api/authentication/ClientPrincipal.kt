package com.sbgcore.oauth.api.authentication

import com.sbgcore.oauth.api.openid.ClientId
import io.ktor.auth.Principal

sealed class ClientPrincipal : Principal {
    abstract val id: ClientId
}

data class ConfidentialClient(override val id: ClientId) : ClientPrincipal()

data class PublicClient(override val id: ClientId) : ClientPrincipal()
