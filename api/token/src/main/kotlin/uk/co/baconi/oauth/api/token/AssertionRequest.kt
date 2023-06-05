package uk.co.baconi.oauth.api.token

import uk.co.baconi.oauth.api.common.client.ClientPrincipal

data class AssertionRequest(
    override val principal: ClientPrincipal,
    val assertion: String // TODO - Define type
) : TokenRequest.Valid<ClientPrincipal> {
    override fun toString(): String {
        return "AssertionRequest(principal=$principal, assertion='REDACTED')"
    }
}