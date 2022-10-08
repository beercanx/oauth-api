package uk.co.baconi.oauth.api.common.client

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

class ClientSecretTest {

    @Nested
    inner class ToString {

        @Test
        fun `should not include secret in toString() output`() {
            assertSoftly(
                ClientSecret(
                    id = UUID.randomUUID(),
                    clientId = ClientId("ConsumerZ"),
                    hashedSecret = "password"
                ).toString()
            ) {
                shouldNotContain("password")
                shouldContain("hashedSecret='REDACTED'")
            }
        }
    }
}