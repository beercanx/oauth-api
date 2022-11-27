package uk.co.baconi.oauth.api.token

import io.ktor.http.*
import uk.co.baconi.oauth.api.common.client.ClientPrincipal
import uk.co.baconi.oauth.api.common.grant.GrantType
import uk.co.baconi.oauth.api.common.scope.ScopesSerializer
import uk.co.baconi.oauth.api.common.token.RefreshTokenService
import uk.co.baconi.oauth.api.common.uuid.UUIDSerializer
import uk.co.baconi.oauth.api.token.TokenErrorType.*

private const val SCOPE = "scope"
private const val REFRESH_TOKEN = "refresh_token"

interface RefreshTokenValidation {

    val refreshTokenService: RefreshTokenService

    fun validateRefreshTokenRequest(parameters: Parameters, client: ClientPrincipal): TokenRequest {

        val uuid = parameters[REFRESH_TOKEN]?.let(UUIDSerializer::fromValueOrNull)
        val scopes = parameters[SCOPE]?.let(ScopesSerializer::deserialize) ?: emptySet()

        return when {
            !client.can(GrantType.RefreshToken) -> TokenRequest.Invalid(
                UnauthorizedClient,
                "not authorized to: ${GrantType.RefreshToken.value}"
            )

            parameters[REFRESH_TOKEN] == null -> TokenRequest.Invalid(
                InvalidRequest,
                "missing parameter: $REFRESH_TOKEN"
            )

            uuid == null -> TokenRequest.Invalid(
                InvalidGrant,
                "invalid parameter: $REFRESH_TOKEN"
            )

            // The requested scope is invalid, unknown, or malformed.
            parameters[SCOPE] != null && scopes.isEmpty() -> TokenRequest.Invalid(
                InvalidScope,
                "invalid parameter: $SCOPE"
            )

            // Check that the Client cannot be issued one of the scopes
            !scopes.all(client::canBeIssued) -> TokenRequest.Invalid(
                InvalidScope,
                "invalid parameter: $SCOPE"
            )

            else -> {
                val token = refreshTokenService.verify(client, uuid)
                when {
                    token == null -> TokenRequest.Invalid(InvalidGrant, "invalid parameter: $REFRESH_TOKEN")
                    parameters[SCOPE] == null -> RefreshTokenRequest(
                        client,
                        token.scopes,
                        token
                    ) // TODO - what if a config change reduces the clients allowed scopes?
                    else -> RefreshTokenRequest(client, scopes.filter(token.scopes::contains).toSet(), token)
                }
            }
        }
    }
}