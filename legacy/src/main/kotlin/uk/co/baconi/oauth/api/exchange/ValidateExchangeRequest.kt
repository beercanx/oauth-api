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

    // TODO - parse GantType similar to scopes, raw vs parsed vs authorised, based on configuration.

    return when (parameters[GRANT_TYPE]?.deserialise<GrantType>()) {

        null -> InvalidConfidentialExchangeRequest(UnsupportedGrantType, "unsupported: ${parameters[GRANT_TYPE]}")
        Assertion -> InvalidConfidentialExchangeRequest(UnsupportedGrantType, "unsupported: ${parameters[GRANT_TYPE]}")
        RefreshToken -> InvalidConfidentialExchangeRequest(
            UnsupportedGrantType,
            "unsupported: ${parameters[GRANT_TYPE]}"
        )

        AuthorisationCode -> {
            val redirectUri = parameters[REDIRECT_URI]
            val code = parameters[CODE]

            when {
                redirectUri == null -> InvalidConfidentialExchangeRequest(
                    InvalidRequest,
                    "missing parameter: redirect_uri"
                )
                redirectUri.isBlank() -> InvalidConfidentialExchangeRequest(
                    InvalidRequest,
                    "invalid parameter: redirect_uri"
                )
                !principal.hasRedirectUri(redirectUri) -> InvalidConfidentialExchangeRequest(
                    InvalidRequest,
                    "invalid parameter: redirect_uri"
                )

                code == null -> InvalidConfidentialExchangeRequest(InvalidRequest, "missing parameter: code")
                code.isBlank() -> InvalidConfidentialExchangeRequest(InvalidRequest, "invalid parameter: code")

                else -> when (val authorisationCode = authorisationCodeService.validate(principal, code, redirectUri)) {
                    null -> InvalidConfidentialExchangeRequest(InvalidGrant)
                    else -> AuthorisationCodeRequest(principal, authorisationCode)
                }
            }
        }

        // TODO - Restrict to only certain clients.
        Password -> {
            val username = parameters[USERNAME]
            val password = parameters[PASSWORD]
            val validScopes = parameters[SCOPE].parseAsScopes(principal)

            when {
                username == null -> InvalidConfidentialExchangeRequest(InvalidRequest, "missing parameter: username")
                username.isBlank() -> InvalidConfidentialExchangeRequest(InvalidRequest, "invalid parameter: username")

                password == null -> InvalidConfidentialExchangeRequest(InvalidRequest, "missing parameter: password")
                password.isBlank() -> InvalidConfidentialExchangeRequest(InvalidRequest, "invalid parameter: password")

                // The requested scope is invalid, unknown, or malformed.
                validScopes == null -> InvalidConfidentialExchangeRequest(InvalidScope, "invalid parameter: scope")

                else -> PasswordRequest(principal, validScopes, username, password)
            }
        }

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

suspend fun ApplicationContext.validatePkceExchangeRequest(
    authorisationCodeService: AuthorisationCodeService,
    principal: PublicClient
): PublicExchangeRequest {

    val parameters = call.receive<Parameters>()

    val grantType = parameters[GRANT_TYPE]?.deserialise<GrantType>()
    val redirectUri = parameters[REDIRECT_URI]
    val code = parameters[CODE]
    val codeVerifier = parameters[CODE_VERIFIER]

    return when (grantType) {

        null -> InvalidPublicExchangeRequest(UnsupportedGrantType, "unsupported: ${parameters[GRANT_TYPE]}")
        Assertion -> InvalidPublicExchangeRequest(UnauthorizedClient, "unsupported: $grantType")
        Password -> InvalidPublicExchangeRequest(UnauthorizedClient, "unsupported: $grantType")
        RefreshToken -> InvalidPublicExchangeRequest(UnauthorizedClient, "unsupported: $grantType")

        AuthorisationCode -> when {
            redirectUri == null -> InvalidPublicExchangeRequest(InvalidRequest, "missing parameter: redirect_uri")
            redirectUri.isBlank() -> InvalidPublicExchangeRequest(InvalidRequest, "invalid parameter: redirect_uri")
            !principal.hasRedirectUri(redirectUri) -> InvalidPublicExchangeRequest(
                InvalidRequest,
                "invalid parameter: redirect_uri"
            )

            code == null -> InvalidPublicExchangeRequest(InvalidRequest, "missing parameter: code")
            code.isBlank() -> InvalidPublicExchangeRequest(InvalidRequest, "invalid parameter: code")

            codeVerifier == null -> InvalidPublicExchangeRequest(InvalidRequest, "missing parameter: code_verifier")
            codeVerifier.isBlank() -> InvalidPublicExchangeRequest(InvalidRequest, "invalid parameter: code_verifier")

            else -> when (val authorisationCode =
                authorisationCodeService.validate(principal, code, redirectUri, codeVerifier)) {
                null -> InvalidPublicExchangeRequest(InvalidGrant)
                else -> PkceAuthorisationCodeRequest(principal, authorisationCode)
            }
        }
    }
}
