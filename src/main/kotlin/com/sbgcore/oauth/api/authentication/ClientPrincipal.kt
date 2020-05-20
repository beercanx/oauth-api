package com.sbgcore.oauth.api.authentication

import io.ktor.auth.Principal

sealed class ClientPrincipal : Principal {
    abstract val id: String
}

data class AuthenticatedClient(override val id: String) : ClientPrincipal()

data class PkceClient(override val id: String) : ClientPrincipal()
