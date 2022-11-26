package uk.co.baconi.oauth.api.token

import io.ktor.server.application.*
import io.ktor.server.request.*
import uk.co.baconi.oauth.api.common.client.ClientPrincipal
import uk.co.baconi.oauth.api.common.grant.GrantType
import uk.co.baconi.oauth.api.common.grant.GrantType.*
import uk.co.baconi.oauth.api.token.TokenErrorType.*
import uk.co.baconi.oauth.api.token.TokenRequest.Invalid

private const val GRANT_TYPE = "grant_type"

interface TokenRequestValidation : AuthorisationCodeValidation, PasswordValidation, RefreshTokenValidation {

    suspend fun ApplicationCall.validateTokenRequest(client: ClientPrincipal): TokenRequest {

        val parameters = receiveParameters()

        val grantType = parameters[GRANT_TYPE]?.let(GrantType::fromValueOrNull)

        return when {
            parameters[GRANT_TYPE] == null -> Invalid(InvalidRequest, "missing parameter: $GRANT_TYPE")
            grantType == null -> Invalid(UnsupportedGrantType, "unsupported: ${parameters[GRANT_TYPE]}")

            !client.can(grantType) -> Invalid(UnauthorizedClient, "not authorized to: ${parameters[GRANT_TYPE]}")

            else -> when (grantType) {
                AuthorisationCode -> validateAuthorisationCodeRequest(parameters, client)
                Password -> validatePasswordRequest(parameters, client)
                RefreshToken -> validateRefreshTokenRequest(parameters, client)
                //Assertion -> validateAssertionRequest(parameters, client)
            }
        }
    }
}