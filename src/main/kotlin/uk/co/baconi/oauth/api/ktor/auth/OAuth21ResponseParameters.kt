package uk.co.baconi.oauth.api.ktor.auth

import io.ktor.server.auth.OAuth2ResponseParameters

/**
 * OAuth 2.1 Response Parameters
 * @see OAuth2ResponseParameters
 */
object OAuth21ResponseParameters {
    /**
     * The scope attribute is a space-delimited list of case-sensitive scope values indicating the required
     * scope of the access token for accessing the requested resource.
     *
     * https://www.ietf.org/archive/id/draft-parecki-oauth-v2-1-01.html#name-access-token-scope
     */
    const val Scope: String = "scope"
}