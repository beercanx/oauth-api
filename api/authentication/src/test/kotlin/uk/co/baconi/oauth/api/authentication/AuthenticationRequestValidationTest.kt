package uk.co.baconi.oauth.api.authentication

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.api.authentication.AuthenticationRequest.*
import uk.co.baconi.oauth.api.common.authentication.AuthenticateSession
import java.util.UUID

class AuthenticationRequestValidationTest {

    private val underTest = object : AuthenticationRequestValidation {}
    private val validCsrfToken = UUID.randomUUID()
    private val validSession = AuthenticateSession(validCsrfToken)

    @Nested
    inner class CsrfToken {

        @Test
        fun `should reject on null authenticate session`() {

            val raw = Raw(username = null, password = null, csrfToken = validCsrfToken.toString())

            assertSoftly(underTest.validateAuthenticationRequest(raw, session = null)) {
                shouldBeInstanceOf<InvalidField>()
                name shouldBe "csrfToken"
            }
        }

        @Test
        fun `should reject on null csrf tokens`() {

            val raw = Raw(username = null, password = null, csrfToken = null)

            assertSoftly(underTest.validateAuthenticationRequest(raw, validSession)) {
                shouldBeInstanceOf<InvalidField>()
                name shouldBe "csrfToken"
            }
        }

        @Test
        fun `should reject on blank csrf tokens`() {

            val raw = Raw(username = null, password = null, csrfToken = " ")

            assertSoftly(underTest.validateAuthenticationRequest(raw, validSession)) {
                shouldBeInstanceOf<InvalidField>()
                name shouldBe "csrfToken"
            }
        }

        @Test
        fun `should reject when the csrf token doesn't match the session value`() {

            val raw = Raw(username = null, password = null, csrfToken = UUID.randomUUID().toString())

            assertSoftly(underTest.validateAuthenticationRequest(raw, validSession)) {
                shouldBeInstanceOf<InvalidField>()
                name shouldBe "csrfToken"
            }
        }
    }

    @Nested
    inner class Username {

        @Test
        fun `should reject on null username`() {

            val raw = Raw(username = null, password = charArrayOf('a'), csrfToken = validCsrfToken.toString())

            assertSoftly(underTest.validateAuthenticationRequest(raw, validSession)) {
                shouldBeInstanceOf<InvalidField>()
                name shouldBe "username"
            }
        }

        @Test
        fun `should reject on blank username`() {

            val raw = Raw(username = " ", password = charArrayOf('a'), csrfToken = validCsrfToken.toString())

            assertSoftly(underTest.validateAuthenticationRequest(raw, validSession)) {
                shouldBeInstanceOf<InvalidField>()
                name shouldBe "username"
            }
        }
    }

    @Nested
    inner class Password {

        @Test
        fun `should reject on null password`() {

            val raw = Raw(username = "aardvark", password = null, csrfToken = validCsrfToken.toString())

            assertSoftly(underTest.validateAuthenticationRequest(raw, validSession)) {
                shouldBeInstanceOf<InvalidField>()
                name shouldBe "password"
            }
        }

        @Test
        fun `should reject on empty password`() {

            val raw = Raw(username = "aardvark", password = charArrayOf(), csrfToken = validCsrfToken.toString())

            assertSoftly(underTest.validateAuthenticationRequest(raw, validSession)) {
                shouldBeInstanceOf<InvalidField>()
                name shouldBe "password"
            }
        }
    }

    @Nested
    inner class Success {

        @Test
        fun `should accept on valid details`() {

            val raw = Raw(username = "aardvark", password = charArrayOf('a'), csrfToken = validCsrfToken.toString())

            assertSoftly(underTest.validateAuthenticationRequest(raw, validSession)) {
                shouldBeInstanceOf<Valid>()
                username shouldBe "aardvark"
                password shouldBe charArrayOf('a')
            }
        }
    }
}