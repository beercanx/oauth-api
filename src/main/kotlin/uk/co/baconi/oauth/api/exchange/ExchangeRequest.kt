package uk.co.baconi.oauth.api.exchange

import uk.co.baconi.oauth.api.authorisation.AuthorisationCode
import uk.co.baconi.oauth.api.client.ConfidentialClient
import uk.co.baconi.oauth.api.client.PublicClient
import uk.co.baconi.oauth.api.scopes.Scopes

sealed class ConfidentialExchangeRequest

data class InvalidConfidentialExchangeRequest(val error: ErrorType, val description: String? = null) : ConfidentialExchangeRequest()

sealed class ValidConfidentialExchangeRequest : ConfidentialExchangeRequest() {
    abstract val principal: ConfidentialClient
}

data class AuthorisationCodeRequest(
    override val principal: ConfidentialClient,
    val code: AuthorisationCode
) : ValidConfidentialExchangeRequest()

data class PasswordRequest(
    override val principal: ConfidentialClient,
    val scopes: Set<Scopes>,
    val username: String,
    val password: String
) : ValidConfidentialExchangeRequest() {
    override fun toString(): String {
        return "PasswordRequest(principal=$principal, scopes=$scopes, username='$username', password=REDACTED)"
    }
}

data class RefreshTokenRequest(
    override val principal: ConfidentialClient,
    val scopes: Set<Scopes>,
    val refreshToken: String
) : ValidConfidentialExchangeRequest() {
    override fun toString(): String {
        return "RefreshTokenRequest(principal=$principal, scopes=$scopes, refreshToken=REDACTED)"
    }
}

data class AssertionRequest(
    override val principal: ConfidentialClient,
    val assertion: String
) : ValidConfidentialExchangeRequest() {
    override fun toString(): String {
        return "AssertionRequest(principal=$principal, assertion=REDACTED)"
    }
}

sealed class PublicExchangeRequest

data class InvalidPublicExchangeRequest(val error: ErrorType, val description: String? = null) : PublicExchangeRequest()

sealed class ValidPublicExchangeRequest : PublicExchangeRequest() {
    abstract val principal: PublicClient
}

data class PkceAuthorisationCodeRequest(
    override val principal: PublicClient,
    val code: AuthorisationCode
) : ValidPublicExchangeRequest()
