package uk.co.baconi.oauth.api.token

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import io.mockk.mockk
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.common.authentication.AuthenticatedUsername
import uk.co.baconi.oauth.api.common.authorisation.AuthorisationCode.Basic
import uk.co.baconi.oauth.api.common.client.ClientId
import java.time.Instant.now
import java.util.*

class AuthorisationCodeRequestTest {

    @Nested
    inner class ToString {

        @Test
        fun `should not include access token value`() {
            val value = UUID.randomUUID()
            assertSoftly(
                AuthorisationCodeRequest(
                    principal = mockk(),
                    code = Basic(value, now(), now(), ClientId(""), AuthenticatedUsername(""), "", emptySet())
                ).toString()
            ) {
                shouldNotContain(value.toString())
                shouldContain("code='REDACTED'")
            }
        }
    }
}