package uk.co.baconi.oauth.automation.api.token.introspection

import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.automation.api.*
import uk.co.baconi.oauth.automation.api.TOKEN_INTROSPECTION

@Tag(RFC7662)
@Tag(TOKEN_INTROSPECTION)
class TokenIntrospectionRequests {

    @Test
    fun `should allow only post requests`() {

    }

    @Test
    fun `should allow only url encoded form requests`() {

    }

    /**
     * To prevent token scanning attacks, the endpoint MUST also require some form of authorization to access this
     * endpoint, such as client authentication as described in OAuth 2.0 [RFC6749] or a separate OAuth 2.0 access token
     * such as the bearer token described in OAuth 2.0 Bearer Token Usage [RFC6750].
     */
    @Test
    fun `must allow only authorised requests`() {

    }
}