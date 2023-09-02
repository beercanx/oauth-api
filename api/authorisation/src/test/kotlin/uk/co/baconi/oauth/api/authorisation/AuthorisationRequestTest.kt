package uk.co.baconi.oauth.api.authorisation

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.api.authorisation.AuthorisationRequest.Basic
import uk.co.baconi.oauth.api.authorisation.AuthorisationRequest.Invalid
import uk.co.baconi.oauth.api.common.authorisation.AuthorisationResponseType.Code
import uk.co.baconi.oauth.api.common.client.ClientId

class AuthorisationRequestTest {

    @Test
    fun `Invalid should not include state in toString`() {
        assertSoftly(Invalid("redirectUri", "error", "description", state = "12345").toString()) {
            shouldNotContain("12345")
            shouldContain("state='REDACTED'")
        }
    }

    @Test
    fun `Valid should not include state in toString`() {
        assertSoftly(Basic(Code, ClientId("clientId"), "redirectUri", state = "abcde", emptySet()).toString()) {
            shouldNotContain("abcde")
            shouldContain("state='REDACTED'")
        }
    }
}
