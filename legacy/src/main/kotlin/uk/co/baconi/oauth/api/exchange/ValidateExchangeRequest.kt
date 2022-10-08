package uk.co.baconi.oauth.api.exchange

import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.request.*
import uk.co.baconi.oauth.api.authorisation.AuthorisationCodeService
import uk.co.baconi.oauth.api.client.ConfidentialClient
import uk.co.baconi.oauth.api.client.PublicClient
import uk.co.baconi.oauth.api.enums.deserialise
import uk.co.baconi.oauth.api.exchange.ErrorType.*
import uk.co.baconi.oauth.api.exchange.GrantType.*
import uk.co.baconi.oauth.api.ktor.ApplicationContext
import uk.co.baconi.oauth.api.scopes.parseAsScopes

private const val GRANT_TYPE = "grant_type"
private const val CODE = "code"
private const val REDIRECT_URI = "redirect_uri"
private const val CODE_VERIFIER = "code_verifier"
private const val SCOPE = "scope"
private const val USERNAME = "username"
private const val PASSWORD = "password"
private const val REFRESH_TOKEN = "refresh_token"
private const val ASSERTION = "assertion"

suspend fun ApplicationContext.validateExchangeRequest(
    authorisationCodeService: AuthorisationCodeService,
    principal: ConfidentialClient
): ConfidentialExchangeRequest {

    val parameters = call.receiveParameters()

    return when (parameters[GRANT_TYPE]?.deserialise<GrantType>()) {
//        RefreshToken -> {
//            val refreshToken = parameters[REFRESH_TOKEN]
//            val validScopes = parameters[SCOPE].parseAsScopes(principal)
//
//            when {
//                refreshToken == null -> InvalidConfidentialExchangeRequest(InvalidRequest, "missing parameter: refresh_token")
//                refreshToken.isBlank() -> InvalidConfidentialExchangeRequest(InvalidRequest, "invalid parameter: refresh_token")
//
//                // The requested scope is invalid, unknown, or malformed.
//                validScopes == null -> InvalidConfidentialExchangeRequest("invalid_scope", "invalid parameter: scope")
//
//                else -> RefreshTokenRequest(principal, validScopes, refreshToken)
//            }
//        }
//
//        Assertion -> {
//            val assertion = parameters[ASSERTION]
//
//            when {
//                assertion == null -> InvalidConfidentialExchangeRequest(InvalidRequest, "missing parameter: assertion")
//                assertion.isBlank() -> InvalidConfidentialExchangeRequest(InvalidRequest, "invalid parameter: assertion")
//
//                else -> AssertionRequest(principal, assertion)
//            }
//        }
    }
}