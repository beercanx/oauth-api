package uk.co.baconi.oauth.api.common.authentication

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.api.common.authentication.CustomerAuthentication.Failure
import uk.co.baconi.oauth.api.common.authentication.CustomerAuthentication.Failure.Reason.Locked
import uk.co.baconi.oauth.api.common.authentication.CustomerAuthentication.Success

/**
 * Testing my polymorphic nature of the [CustomerAuthentication] when serialising with [kotlinx.serialization]
 */
class CustomerAuthenticationIntegrationTest {

    private val json = Json

    @Nested
    inner class Serialising {

        @Test
        fun `should include type of success in serialised Success instances`() {
            json.encodeToString<CustomerAuthentication>(Success(AuthenticatedUsername("aardvark"))) shouldBe """
                {"type":"success","username":"aardvark"}
            """.trimIndent()
        }

        @Test
        fun `should include type of failure in serialised Failure instances`() {
            json.encodeToString<CustomerAuthentication>(Failure()) shouldBe """
                {"type":"failure"}
            """.trimIndent()
        }

        @Test
        fun `should not include reason in serialised Failure instances`() {
            json.encodeToString<CustomerAuthentication>(Failure(reason = Locked)) shouldBe """
                {"type":"failure"}
            """.trimIndent()
        }
    }

    @Nested
    inner class Deserialising {

        @Test
        fun `should be able to receive success types as Success`() {
            assertSoftly(json.decodeFromString<CustomerAuthentication>("""{"type":"success","username":"badger"}""")) {
                shouldBeInstanceOf<Success>()
                username shouldBe AuthenticatedUsername("badger")
            }
        }

        @Test
        fun `should be able to receive failure types as Failure`() {
            assertSoftly(json.decodeFromString<CustomerAuthentication>("""{"type":"failure"}""")) {
                shouldBeInstanceOf<Failure>()
            }
        }
    }
}