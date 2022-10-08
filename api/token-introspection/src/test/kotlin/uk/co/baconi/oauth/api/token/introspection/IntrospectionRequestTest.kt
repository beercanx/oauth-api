package uk.co.baconi.oauth.api.token.introspection

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.api.common.client.ClientConfiguration
import uk.co.baconi.oauth.api.common.client.ClientId
import uk.co.baconi.oauth.api.common.client.ClientType
import uk.co.baconi.oauth.api.common.client.ConfidentialClient
import java.util.*

class IntrospectionRequestTest {

    @Nested
    inner class Valid {

        @Test
        fun `should not include token value`() {

            val underTest = IntrospectionRequest.Valid(
                principal = ConfidentialClient(
                    ClientConfiguration(
                        id = ClientId("consumer-z"),
                        type = ClientType.Confidential,
                        redirectUris = emptySet(),
                        allowedScopes = emptySet(),
                        allowedActions = emptySet(),
                        allowedGrantTypes = emptySet(),
                    )
                ),
                token = UUID.randomUUID()
            )

            assertSoftly(underTest.toString()) {
                shouldNotContain(underTest.token.toString())
                shouldContain("token='REDACTED'")
            }
        }
    }
}