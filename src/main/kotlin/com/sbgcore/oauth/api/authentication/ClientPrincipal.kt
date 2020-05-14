package com.sbgcore.oauth.api.authentication

import io.ktor.auth.Principal

sealed class ClientPrincipal : Principal {
    abstract val id: String
}

data class AuthenticatedClientPrincipal(override val id: String) : ClientPrincipal()

data class PkceClientPrincipal(override val id: String) : ClientPrincipal()
