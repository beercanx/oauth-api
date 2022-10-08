package uk.co.baconi.oauth.api.exchange

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import uk.co.baconi.oauth.api.client.ConfidentialClient
import uk.co.baconi.oauth.api.client.PublicClient
import uk.co.baconi.oauth.api.enums.deserialise
import uk.co.baconi.oauth.api.exchange.GrantType.AuthorisationCode
import uk.co.baconi.oauth.api.exchange.GrantType.Password
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

suspend fun ApplicationContext.validateExchangeRequest(principal: ConfidentialClient): ConfidentialExchangeRequest {

    val parameters = call.receiveParameters()

    return when (parameters[GRANT_TYPE]?.deserialise<GrantType>()) {

        AuthorisationCode -> {
            val redirectUri = parameters[REDIRECT_URI]
            val code = parameters[CODE]

            when {
                redirectUri == null -> InvalidConfidentialExchangeRequest // TODO - Missing Parameter: redirect_uri
                redirectUri.isBlank() -> InvalidConfidentialExchangeRequest // TODO - Invalid Parameter: redirect_uri
                !principal.hasRedirectUri(redirectUri) -> InvalidConfidentialExchangeRequest // TODO - Invalid Parameter: redirect_uri

                code == null -> InvalidConfidentialExchangeRequest // TODO - Missing Parameter: code
                code.isBlank() -> InvalidConfidentialExchangeRequest // TODO - Invalid Parameter: code

                // TODO - Validate the [code] is a valid code via a repository
                // TODO - Validate the [redirect_uri] is the same as what was used to generate the [code]
                // TODO - Validate the [client_id] is the same as what was used to generate the [code]
                // TODO - Replace code with AuthorisationCode object

                else -> AuthorisationCodeRequest(principal, code, redirectUri)
            }
        }

        Password -> {
            val username = parameters[USERNAME]
            val password = parameters[PASSWORD]
            val (rawMatchedParsed, parsedMatchedValid, validScopes) = parameters[SCOPE].parseAsScopes(principal)

            when {
                username == null -> InvalidConfidentialExchangeRequest // TODO - Missing Parameter: username
                username.isBlank() -> InvalidConfidentialExchangeRequest // TODO - Invalid Parameter: username

                password == null -> InvalidConfidentialExchangeRequest // TODO - Missing Parameter: password
                password.isBlank() -> InvalidConfidentialExchangeRequest // TODO - Invalid Parameter: password

                // TODO - Do we reject if the scope parameter is missing?

                // The requested scope is invalid, unknown, or malformed.
                !rawMatchedParsed -> InvalidConfidentialExchangeRequest // TODO - Invalid Parameter: scope

                // TODO - Do we reject if the scope parsed size is different from the valid size?
                !parsedMatchedValid -> InvalidConfidentialExchangeRequest // TODO - Invalid Parameter: scope

                else -> PasswordRequest(principal, validScopes, username, password)
            }
        }

        // TODO - Work out why refresh token grant can request scopes, but only less than what's issued?
//        RefreshToken -> {
//            val refreshToken = parameters[REFRESH_TOKEN]
//            val (rawScopes, parsedScopes, validScopes) = parameters[SCOPE].parseAsScopes(principal)
//
//            when {
//                refreshToken == null -> InvalidConfidentialExchangeRequest // TODO - Missing Parameter: refresh_token
//                refreshToken.isBlank() -> InvalidConfidentialExchangeRequest // TODO - Invalid Parameter: refresh_token
//
//                // TODO - Do we reject if the scope parameter is missing?
//
//                // The requested scope is invalid, unknown, or malformed.
//                !rawMatchedParsed -> InvalidConfidentialExchangeRequest // TODO - Invalid Parameter: scope
//
//                // TODO - Do we reject if the scope parsed size is different from the valid size?
//                !parsedMatchedValid -> InvalidConfidentialExchangeRequest // TODO - Invalid Parameter: scope
//
//                else -> RefreshTokenRequest(principal, validScopes, refreshToken)
//            }
//        }
//
//        Assertion -> {
//            val assertion = parameters[ASSERTION]
//
//            when {
//                assertion == null -> InvalidConfidentialExchangeRequest // TODO - Missing Parameter: assertion
//                assertion.isBlank() -> InvalidConfidentialExchangeRequest // TODO - Invalid Parameter: assertion
//
//                else -> AssertionRequest(principal, assertion)
//            }
//        }

        else -> InvalidConfidentialExchangeRequest // TODO - 'unsupported_grant_type'
    }
}

suspend fun ApplicationContext.validatePkceExchangeRequest(principal: PublicClient): PublicExchangeRequest {

    val parameters = call.receive<Parameters>()

    val grantType = parameters[GRANT_TYPE]?.deserialise<GrantType>()
    val redirectUri = parameters[REDIRECT_URI]
    val code = parameters[CODE]
    val codeVerifier = parameters[CODE_VERIFIER]

    return when (grantType) {
        AuthorisationCode -> when {
            redirectUri == null -> InvalidPublicExchangeRequest // TODO - Missing Parameter: redirect_uri
            redirectUri.isBlank() -> InvalidPublicExchangeRequest // TODO - Invalid Parameter: redirect_uri
            !principal.hasRedirectUri(redirectUri) -> InvalidPublicExchangeRequest // TODO - Invalid Parameter: redirect_uri

            code == null -> InvalidPublicExchangeRequest // TODO - Missing Parameter: code
            code.isBlank() -> InvalidPublicExchangeRequest // TODO - Invalid Parameter: code

            codeVerifier == null -> InvalidPublicExchangeRequest // TODO - Missing Parameter: code_verifier
            codeVerifier.isBlank() -> InvalidPublicExchangeRequest // TODO - Invalid Parameter: code_verifier

            else -> PkceAuthorisationCodeRequest(principal, code, redirectUri, codeVerifier)
        }

        else -> InvalidPublicExchangeRequest // TODO - 'unsupported_grant_type'
    }
}
