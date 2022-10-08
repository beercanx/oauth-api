package uk.co.baconi.oauth.api.token

import uk.co.baconi.oauth.api.common.client.ClientPrincipal

sealed interface TokenRequest {

    data class Invalid(val error: TokenErrorType, val description: String) : TokenRequest {
        fun toResponse(): TokenResponse.Failed = TokenResponse.Failed(error, description)
    }

    sealed interface Valid<A : ClientPrincipal> : TokenRequest {
        val principal: A
    }
}



