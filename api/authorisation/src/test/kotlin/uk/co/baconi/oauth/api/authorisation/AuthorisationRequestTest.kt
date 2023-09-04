package uk.co.baconi.oauth.api.authorisation

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.api.authorisation.AuthorisationRequest.*
import uk.co.baconi.oauth.api.common.authorisation.AuthorisationResponseType.Code
import uk.co.baconi.oauth.api.common.authorisation.CodeChallenge
import uk.co.baconi.oauth.api.common.authorisation.CodeChallengeMethod.S256
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
    fun `Basic should not include state in toString`() {
        assertSoftly(Basic(Code, ClientId("clientId"), "redirectUri", state = "abcde", emptySet()).toString()) {
            shouldNotContain("abcde")
            shouldContain("state='REDACTED'")
        }
    }

    @Test
    fun `PKCE should not include state or code challenge in toString`() {
        val base = Basic(Code, ClientId("clientId"), "redirectUri", state = "abcde", emptySet())
        assertSoftly(PKCE(base, CodeChallenge("1234"), S256).toString()) {
            shouldNotContain("abcde")
            shouldContain("state='REDACTED'")
            shouldNotContain("1234")
            shouldContain("codeChallenge='REDACTED'")
        }
    }
}
