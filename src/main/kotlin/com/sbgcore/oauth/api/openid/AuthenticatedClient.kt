package com.sbgcore.oauth.api.openid

import io.ktor.auth.Principal

data class AuthenticatedClient(val id: String) : Principal