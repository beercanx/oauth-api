package uk.co.baconi.oauth.api.token.introspection

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.throwable.shouldHaveMessage
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import uk.co.baconi.oauth.api.common.authentication.AuthenticatedUsername
import uk.co.baconi.oauth.api.common.client.ClientId
import uk.co.baconi.oauth.api.common.token.TokenType.Bearer
import uk.co.baconi.oauth.api.token.introspection.IntrospectionErrorType.InvalidRequest
import uk.co.baconi.oauth.api.token.introspection.IntrospectionErrorType.UnauthorizedClient

class IntrospectionResponseTests {

    private val json = Json {
        encodeDefaults = true
    }

    @Nested
    inner class Active {

        @Test
        fun `should never have an active field of false`() {

            assertThrows<IllegalArgumentException> {
                IntrospectionResponse.Active(
                    active = false,
                    scope = emptySet(),
                    clientId = ClientId("aardvark"),
                    username = AuthenticatedUsername("badger"),
                    tokenType = Bearer,
                    expirationTime = 1,
                    issuedAt = 0,
                    notBefore = -1,
                    subject = AuthenticatedUsername("badger")
                )
            } shouldHaveMessage "Active response cannot be inactive!"
        }

        @Test
        fun `should always serialise active to be true`() {

            val active = IntrospectionResponse.Active(
                scope = emptySet(),
                clientId = ClientId("aardvark"),
                username = AuthenticatedUsername("badger"),
                tokenType = Bearer,
                expirationTime = 1,
                issuedAt = 0,
                notBefore = -1,
                subject = AuthenticatedUsername("badger")
            )

            json.encodeToString(active) shouldContain """"active":true"""
        }
    }

    @Nested
    inner class Inactive {

        @Test
        fun `should never have an active field of true`() {

            assertThrows<IllegalArgumentException> {
                IntrospectionResponse.Inactive(
                    active = true
                )
            } shouldHaveMessage "Inactive response cannot be active!"
        }

        @Test
        fun `should always serialise active to be false`() {

            val inactive = IntrospectionResponse.Inactive()

            json.encodeToString(inactive) shouldBe """{"active":false}"""
        }
    }

    @Nested
    inner class Invalid {

        @Test
        fun `should not have an empty description`() {

            assertThrows<IllegalArgumentException> {
                IntrospectionResponse.Invalid(
                    error = InvalidRequest,
                    errorDescription = " "
                )
            } shouldHaveMessage "Error description should not be blank!"
        }

        @Test
        fun `should always serialise error and description`() {

            val invalid = IntrospectionResponse.Invalid(
                error = UnauthorizedClient,
                errorDescription = "client is not allowed to introspect"
            )

            json.encodeToString(invalid) shouldBe """{"error":"unauthorized_client","error_description":"client is not allowed to introspect"}"""
        }
    }
}