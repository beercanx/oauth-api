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

    @Nested
    inner class EqualsAndHashCode {

        private val clientSecret = ClientSecret(
            id = UUID.randomUUID(),
            clientId = ClientId("aardvark"),
            hashedSecret = "HASH"
        )

        @Test
        fun `should be equal if the client configuration ids are identical`() {
            withClue("equalTo") {
                clientSecret shouldBe clientSecret.copy(hashedSecret = "ANOTHER HASH")
            }
        }

        @Test
        fun `should not be equal if the client configuration ids are not identical`() {
            withClue("equalTo") {
                clientSecret shouldNotBe clientSecret.copy(id = UUID.randomUUID())
            }
        }

        @Test
        fun `should have the same hash code if the client configuration ids are identical`() {
            withClue("hashCode") {
                clientSecret.hashCode() shouldBe clientSecret.copy(hashedSecret = "ANOTHER HASH").hashCode()
            }
        }

        @Test
        fun `should not have the same hash code if the client configuration ids are not identical`() {
            withClue("hashCode") {
                clientSecret.hashCode() shouldNotBe clientSecret.copy(id = UUID.randomUUID()).hashCode()
            }
        }
    }
}