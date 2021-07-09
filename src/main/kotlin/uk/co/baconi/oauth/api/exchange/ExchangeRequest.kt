package uk.co.baconi.oauth.api.exchange

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import uk.co.baconi.oauth.api.authorisation.AuthorisationCode
import uk.co.baconi.oauth.api.client.ClientId
import uk.co.baconi.oauth.api.client.ConfidentialClient
import uk.co.baconi.oauth.api.client.PublicClient
import uk.co.baconi.oauth.api.scopes.Scopes
import uk.co.baconi.oauth.api.serializers.ScopeSerializer

@Serializable
data class RawExchangeRequest(
    // All
    @SerialName("grant_type") val grantType: GrantType?,

    // AuthorisationCodeRequest && PkceAuthorisationCodeRequest
    val code: String? = null,
    @SerialName("redirect_uri") val redirectUri: String?,

    // PkceAuthorisationCodeRequest
    @SerialName("code_verifier") val codeVerifier: String?,
    @SerialName("client_id") val clientId: ClientId?,

    // PasswordRequest && RefreshTokenRequest
    @Serializable(with = ScopeSerializer::class) val scope: Set<Scopes>?,

    // PasswordRequest
    val username: String?,
    val password: String?,

    // RefreshTokenRequest
    @SerialName("refresh_token") val refreshToken: String?,

    // AssertionRequest
    val assertion: String?,

    ) {
    override fun toString(): String {
        return "RawExchangeRequest(" +
                "grantType=$grantType, " +
                "code=REDACTED, " +
                "redirectUri=$redirectUri, " +
                "codeVerifier=REDACTED, " +
                "clientId=$clientId, " +
                "scope=$scope, " +
                "username=$username, " +
                "password=REDACTED, " +
                "refreshToken=REDACTED, " +
                "assertion=REDACTED, " +
                ")"
    }
}

sealed class ConfidentialExchangeRequest

// TODO - Extend to support validation failure reasons?
object InvalidConfidentialExchangeRequest : ConfidentialExchangeRequest()

sealed class ValidatedConfidentialExchangeRequest : ConfidentialExchangeRequest() {
    abstract val principal: ConfidentialClient
    override fun toString(): String {
        return "ValidatedConfidentialExchangeRequest(principal=$principal)"
    }
}

sealed class PublicExchangeRequest

// TODO - Extend to support validation failure reasons?
object InvalidPublicExchangeRequest : PublicExchangeRequest()

sealed class ValidatedPublicExchangeRequest : PublicExchangeRequest() {
    abstract val principal: PublicClient
    override fun toString(): String {
        return "ValidatedPublicExchangeRequest(principal=$principal)"
    }
}

data class AuthorisationCodeRequest(
    override val principal: ConfidentialClient,
    val code: AuthorisationCode
) : ValidatedConfidentialExchangeRequest()

data class PkceAuthorisationCodeRequest(
    override val principal: PublicClient,
    val code: String,
    val redirectUri: String,
    val codeVerifier: String
) : ValidatedPublicExchangeRequest() {
    override fun toString(): String {
        return "PkceAuthorisationCodeRequest(principal=$principal, code=REDACTED, redirectUri=$redirectUri, codeVerifier=REDACTED)"
    }
}

data class PasswordRequest(
    override val principal: ConfidentialClient,
    val scopes: Set<Scopes>,
    val username: String,
    val password: String
) : ValidatedConfidentialExchangeRequest() {
    override fun toString(): String {
        return "PasswordRequest(principal=$principal, scopes=$scopes, username='$username', password=REDACTED)"
    }
}

data class RefreshTokenRequest(
    override val principal: ConfidentialClient,
    val scopes: Set<Scopes>,
    val refreshToken: String
) : ValidatedConfidentialExchangeRequest() {
    override fun toString(): String {
        return "RefreshTokenRequest(principal=$principal, scopes=$scopes, refreshToken=REDACTED)"
    }
}

data class AssertionRequest(
    override val principal: ConfidentialClient,
    val assertion: String
) : ValidatedConfidentialExchangeRequest() {
    override fun toString(): String {
        return "AssertionRequest(principal=$principal, assertion=REDACTED)"
    }
}

