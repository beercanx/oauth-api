package uk.co.baconi.oauth.api.token

import io.ktor.http.*
import uk.co.baconi.oauth.api.common.client.ClientPrincipal
import uk.co.baconi.oauth.api.common.grant.GrantType
import uk.co.baconi.oauth.api.token.TokenErrorType.InvalidRequest
import uk.co.baconi.oauth.api.token.TokenErrorType.UnauthorizedClient

private const val ASSERTION = "assertion"

interface AssertionValidation {

    fun validateAssertionRequest(parameters: Parameters, client: ClientPrincipal): TokenRequest {

        val assertion = parameters[ASSERTION]

        return when {
            !client.can(GrantType.Assertion) -> TokenRequest.Invalid(
                UnauthorizedClient,
                "not authorized to: ${GrantType.Assertion.value}"
            )

            assertion == null -> TokenRequest.Invalid(InvalidRequest, "missing parameter: $ASSERTION")
            assertion.isBlank() -> TokenRequest.Invalid(InvalidRequest, "invalid parameter: $ASSERTION")

            // TODO - Look up from a storage mechanism or decode a JWT.
            else -> AssertionRequest(client, assertion)
        }
    }
}