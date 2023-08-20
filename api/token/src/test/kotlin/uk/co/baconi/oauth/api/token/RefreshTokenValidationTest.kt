package uk.co.baconi.oauth.api.token

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.beEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.ktor.http.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import uk.co.baconi.oauth.api.common.client.ClientPrincipal
import uk.co.baconi.oauth.api.common.grant.GrantType
import uk.co.baconi.oauth.api.common.scope.Scope
import uk.co.baconi.oauth.api.common.scope.ScopeRepository
import uk.co.baconi.oauth.api.common.token.RefreshToken
import uk.co.baconi.oauth.api.common.token.RefreshTokenService
import uk.co.baconi.oauth.api.token.TokenErrorType.*
import uk.co.baconi.oauth.api.token.TokenRequest.Invalid
import java.util.*

class RefreshTokenValidationTest {

    private val validRefreshToken = "fff4a0b0-130d-4149-814c-2e5e3d636f99"
    private val mockRefreshToken = mockk<RefreshToken> {
        every { value } returns UUID.fromString(validRefreshToken)
    }

    private val parameters = mockk<Parameters>()

    private val client = mockk<ClientPrincipal> {
        every { can(GrantType.RefreshToken) } returns true
    }

    private val underTest = object : RefreshTokenValidation {
        override val scopeRepository = ScopeRepository()
        override val refreshTokenService = mockk<RefreshTokenService> {
            every { verify(any(), eq(UUID.fromString(validRefreshToken))) } returns mockRefreshToken
        }
    }

    @Test
    fun `should reject clients that are not allowed to refresh tokens`() {

        every { parameters["refresh_token"] } returns null
        every { parameters["scope"] } returns null
        every { client.can(any<GrantType>()) } returns false

        assertSoftly(underTest.validateRefreshTokenRequest(parameters, client)) {
            shouldBeInstanceOf<Invalid>()
            error shouldBe UnauthorizedClient
            description shouldBe "not authorized to: refresh_token"
        }

        verify { client.can(GrantType.RefreshToken)  }
    }

    @Test
    fun `should reject requests with a missing refresh token parameter`() {

        every { parameters["refresh_token"] } returns null
        every { parameters["scope"] } returns "basic"

        assertSoftly(underTest.validateRefreshTokenRequest(parameters, client)) {
            shouldBeInstanceOf<Invalid>()
            error shouldBe InvalidRequest
            description shouldBe "missing parameter: refresh_token"
        }

    }

    @ParameterizedTest
    @CsvSource("null", "aardvark", "123-abc-456-DEF")
    fun `should reject requests with invalid refresh token parameter values`(token: String) {

        every { parameters["refresh_token"] } returns token
        every { parameters["scope"] } returns null

        assertSoftly(underTest.validateRefreshTokenRequest(parameters, client)) {
            shouldBeInstanceOf<Invalid>()
            error shouldBe InvalidGrant
            description shouldBe "invalid parameter: refresh_token"
        }
    }

    @Test
    fun `should reject requests with no valid scopes`() {

        every { parameters["refresh_token"] } returns validRefreshToken
        every { parameters["scope"] } returns "aardvark"

        assertSoftly(underTest.validateRefreshTokenRequest(parameters, client)) {
            shouldBeInstanceOf<Invalid>()
            error shouldBe InvalidScope
            description shouldBe "invalid parameter: scope"
        }
    }

    @Test
    fun `should reject requests with scopes that cannot be issued to the client`() {

        every { parameters["refresh_token"] } returns validRefreshToken
        every { parameters["scope"] } returns "basic"
        every { client.canBeIssued(any()) } returns false

        assertSoftly(underTest.validateRefreshTokenRequest(parameters, client)) {
            shouldBeInstanceOf<Invalid>()
            error shouldBe InvalidScope
            description shouldBe "invalid parameter: scope"
        }

        verify { client.canBeIssued(Scope("basic")) }
    }

    @Test
    fun `should reject requests that fail to be verified`() {

        every { parameters["refresh_token"] } returns "a303522c-8448-4949-96f5-7d3bff02f46b"
        every { parameters["scope"] } returns null
        every { underTest.refreshTokenService.verify(any(), any<UUID>()) } returns null

        assertSoftly(underTest.validateRefreshTokenRequest(parameters, client)) {
            shouldBeInstanceOf<Invalid>()
            error shouldBe InvalidGrant
            description shouldBe "invalid parameter: refresh_token"
        }

        verify { underTest.refreshTokenService.verify(client, UUID.fromString("a303522c-8448-4949-96f5-7d3bff02f46b")) }
    }

    @Test
    fun `should accept requests with no scope change`() {

        every { parameters["refresh_token"] } returns validRefreshToken
        every { parameters["scope"] } returns null
        every { mockRefreshToken.scopes } returns setOf(Scope("basic"))

        assertSoftly(underTest.validateRefreshTokenRequest(parameters, client)) {
            shouldBeInstanceOf<RefreshTokenRequest>()
            principal shouldBe client
            scopes shouldContain Scope("basic")
            refreshToken.value shouldBe UUID.fromString(validRefreshToken)
        }
    }

    @Test
    fun `should accept requests with a reduction in scope`() {

        every { parameters["refresh_token"] } returns validRefreshToken
        every { parameters["scope"] } returns "profile::read"
        every { mockRefreshToken.scopes } returns setOf(Scope("basic"), Scope("profile::read"))
        every { client.canBeIssued(Scope("profile::read")) } returns true

        assertSoftly(underTest.validateRefreshTokenRequest(parameters, client)) {
            shouldBeInstanceOf<RefreshTokenRequest>()
            principal shouldBe client
            scopes shouldContainExactly setOf(Scope("profile::read"))
            refreshToken.value shouldBe UUID.fromString(validRefreshToken)
        }
    }

    @Test
    fun `should do something when the scope change requested is not available in the existing token`() {

        every { parameters["refresh_token"] } returns validRefreshToken
        every { parameters["scope"] } returns "profile::write"
        every { mockRefreshToken.scopes } returns setOf(Scope("basic"), Scope("profile::read"))
        every { client.canBeIssued(Scope("profile::write")) } returns true

        // TODO - Verify if this should work like this or if we should have failed to issue the refresh token.
        assertSoftly(underTest.validateRefreshTokenRequest(parameters, client)) {
            shouldBeInstanceOf<RefreshTokenRequest>()
            principal shouldBe client
            scopes should beEmpty()
            refreshToken.value shouldBe UUID.fromString(validRefreshToken)
        }
    }
}