package uk.co.baconi.oauth.automation.api.token.introspection

import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.automation.api.*
import uk.co.baconi.oauth.automation.api.TOKEN_INTROSPECTION

@Tag(RFC7662)
@Tag(AUTOMATION)
@Tag(TOKEN_INTROSPECTION)
class TokenIntrospectionResponses {

    /**
     * The introspection endpoint is an OAuth 2.0 endpoint that takes a parameter representing an OAuth 2.0 token and
     * returns a JSON [RFC7159] document representing the meta information surrounding the token, including whether
     * this token is currently active.
     */
    @Test
    fun `should return in JSON`() {

    }
}