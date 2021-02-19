package com.sbgcore.oauth.api.authentication

import io.ktor.auth.Principal

sealed class ClientPrincipal : Principal {
    abstract val id: String
}

data class ConfidentialClient(override val id: String) : ClientPrincipal()

data class PublicClient(override val id: String) : ClientPrincipal()
