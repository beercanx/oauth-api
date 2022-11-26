package uk.co.baconi.oauth.api.exchange

import uk.co.baconi.oauth.api.authorisation.AuthorisationCode
import uk.co.baconi.oauth.api.client.ConfidentialClient
import uk.co.baconi.oauth.api.client.PublicClient
import uk.co.baconi.oauth.api.scopes.Scopes

data class AssertionRequest(
    override val principal: ConfidentialClient,
    val assertion: String
) : ValidConfidentialExchangeRequest() {
    override fun toString(): String {
        return "AssertionRequest(principal=$principal, assertion=REDACTED)"
    }
}
