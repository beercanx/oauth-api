package uk.co.baconi.oauth.api.token

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.string.beBlank
import io.kotest.matchers.types.shouldBeInstanceOf
import io.ktor.http.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.api.common.client.ConfidentialClient
import uk.co.baconi.oauth.api.common.client.PublicClient
import uk.co.baconi.oauth.api.common.grant.GrantType.Password
import uk.co.baconi.oauth.api.common.scope.Scope
import uk.co.baconi.oauth.api.common.scope.ScopeRepository

import uk.co.baconi.oauth.api.token.TokenErrorType.*

class PasswordValidationTest : PasswordValidation {

    override val scopeRepository = ScopeRepository()

    private val client = mockk<ConfidentialClient> {
        every { can(Password) } returns true
        every { canBeIssued(any()) } returns true
    }
    private val parameters = mockk<Parameters> {
        every { this@mockk["username"] } returns "aardvark"
        every { this@mockk["password"] } returns "badger"
        every { this@mockk["scope"] } returns "basic"
    }

    @Test
    fun `should return invalid request for a non confidential client`() {

        val publicClient = mockk<PublicClient> {
            every { can(Password) } returns false
        }

        assertSoftly(validatePasswordRequest(parameters, publicClient)) {
            shouldBeInstanceOf<TokenRequest.Invalid>()
            error shouldBe UnauthorizedClient
            description shouldNot beBlank()
        }

        verify(exactly = 0) { publicClient.can(Password) }
    }

    @Test
    fun `should return invalid request for an unauthorised client`() {

        every { client.can(Password) } returns false

        assertSoftly(validatePasswordRequest(parameters, client)) {
            shouldBeInstanceOf<TokenRequest.Invalid>()
            error shouldBe UnauthorizedClient
            description shouldNot beBlank()
        }

        verify { client.can(Password) }
    }

    @Test
    fun `should return invalid request on missing username`() {

        every { parameters["username"] } returns null

        assertSoftly(validatePasswordRequest(parameters, client)) {
            shouldBeInstanceOf<TokenRequest.Invalid>()
            error shouldBe InvalidRequest
            description shouldBe "missing parameter: username"
        }
    }

    @Test
    fun `should return invalid request on blank username`() {

        every { parameters["username"] } returns ""

        assertSoftly(validatePasswordRequest(parameters, client)) {
            shouldBeInstanceOf<TokenRequest.Invalid>()
            error shouldBe InvalidRequest
            description shouldBe "invalid parameter: username"
        }
    }

    @Test
    fun `should return invalid request on missing password`() {

        every { parameters["password"] } returns null

        assertSoftly(validatePasswordRequest(parameters, client)) {
            shouldBeInstanceOf<TokenRequest.Invalid>()
            error shouldBe InvalidRequest
            description shouldBe "missing parameter: password"
        }
    }

    @Test
    fun `should return invalid request on invalid scope`() {

        every { parameters["scope"] } returns "Cicada"

        assertSoftly(validatePasswordRequest(parameters, client)) {
            shouldBeInstanceOf<TokenRequest.Invalid>()
            error shouldBe InvalidScope
            description shouldBe "invalid parameter: scope"
        }

        verify(exactly = 0) { client.canBeIssued(any()) }
    }

    @Test
    fun `should return invalid request on unauthorised scopes`() {

        every { client.canBeIssued(any()) } returns false

        assertSoftly(validatePasswordRequest(parameters, client)) {
            shouldBeInstanceOf<TokenRequest.Invalid>()
            error shouldBe InvalidScope
            description shouldBe "invalid parameter: scope"
        }

        verify { client.canBeIssued(any()) }
    }

    @Test
    fun `should return a valid request if scope is not provided`() {

        every { parameters["scope"] } returns null

        assertSoftly(validatePasswordRequest(parameters, client)) {
            shouldBeInstanceOf<PasswordRequest>()
            principal shouldBe client
            username shouldBe "aardvark"
            password shouldBe "badger".toCharArray()
            scopes shouldBe emptySet()
        }
    }

    @Test
    fun `should return a valid request`() {

        assertSoftly(validatePasswordRequest(parameters, client)) {
            shouldBeInstanceOf<PasswordRequest>()
            principal shouldBe client
            username shouldBe "aardvark"
            password shouldBe "badger".toCharArray()
            scopes shouldContainExactly setOf(Scope("basic"))
        }
    }
}