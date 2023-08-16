package uk.co.baconi.oauth.api.token

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.api.common.client.ClientPrincipal
import uk.co.baconi.oauth.api.common.token.RefreshToken
import java.util.UUID

class AssertionRequestTest {

    @Nested
    inner class ToString {

        @Test
        fun `should not include assertion value`() {
            val uuid = UUID.randomUUID()
            assertSoftly(
                AssertionRequest(
                    principal = mockk<ClientPrincipal>(),
                    assertion = uuid.toString()
                ).toString()
            ) {
                shouldNotContain("$uuid")
                shouldContain("assertion='REDACTED'")
            }
        }
    }
}