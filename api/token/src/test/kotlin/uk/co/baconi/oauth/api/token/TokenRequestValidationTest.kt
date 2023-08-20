package uk.co.baconi.oauth.api.token

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.string.beBlank
import io.kotest.matchers.types.shouldBeInstanceOf
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.api.common.authorisation.AuthorisationCodeRepository
import uk.co.baconi.oauth.api.common.client.ClientAction
import uk.co.baconi.oauth.api.common.client.ClientPrincipal
import uk.co.baconi.oauth.api.common.grant.GrantType
import uk.co.baconi.oauth.api.common.grant.GrantType.Password
import uk.co.baconi.oauth.api.common.scope.ScopeRepository
import uk.co.baconi.oauth.api.common.token.RefreshTokenService
import uk.co.baconi.oauth.api.token.TokenErrorType.*

class TokenRequestValidationTest : TokenRequestValidation {

    override val scopeRepository = ScopeRepository()

    init {
        // Enables mocking receive<Parameters>() extension
        mockkStatic("io.ktor.server.request.ApplicationReceiveFunctionsKt")
    }

    private val client = mockk<ClientPrincipal> {
        every { can(any<ClientAction>()) } returns false
        every { can(any<GrantType>()) } returns false
    }

    private val parameters = mockk<Parameters>()
    private val call = mockk<ApplicationCall> {
        coEvery { receive<Parameters>() } returns this@TokenRequestValidationTest.parameters
    }

    private val tokenRequest = mockk<TokenRequest>()
    override val authorisationCodeRepository = mockk<AuthorisationCodeRepository>()
    override val refreshTokenService = mockk<RefreshTokenService>()

    override fun validateAssertionRequest(parameters: Parameters, client: ClientPrincipal): TokenRequest {
        return tokenRequest
    }

    override fun validateAuthorisationCodeRequest(parameters: Parameters, client: ClientPrincipal): TokenRequest {
        return tokenRequest
    }

    override fun validatePasswordRequest(parameters: Parameters, client: ClientPrincipal): TokenRequest {
        return tokenRequest
    }

    override fun validateRefreshTokenRequest(parameters: Parameters, client: ClientPrincipal): TokenRequest {
        return tokenRequest
    }

    @Test
    fun `should return invalid request on missing grant type`(): Unit = runBlocking {

        every { parameters["grant_type"] } returns null

        assertSoftly(call.validateTokenRequest(client)) {
            shouldBeInstanceOf<TokenRequest.Invalid>()
            error shouldBe InvalidRequest
            description shouldNot beBlank()
        }
    }

    @Test
    fun `should return invalid request on blank grant type`(): Unit = runBlocking {

        every { parameters["grant_type"] } returns "     "

        assertSoftly(call.validateTokenRequest(client)) {
            shouldBeInstanceOf<TokenRequest.Invalid>()
            error shouldBe UnsupportedGrantType
            description shouldNot beBlank()
        }
    }

    @Test
    fun `should return invalid request on invalid grant type`(): Unit = runBlocking {

        every { parameters["grant_type"] } returns "aardvark"

        assertSoftly(call.validateTokenRequest(client)) {
            shouldBeInstanceOf<TokenRequest.Invalid>()
            error shouldBe UnsupportedGrantType
            description shouldNot beBlank()
        }
    }

    @Test
    fun `should return invalid request on client unauthorised to use grant type`(): Unit = runBlocking {

        every { parameters["grant_type"] } returns "password"
        every { client.can(Password) } returns false

        assertSoftly(call.validateTokenRequest(client)) {
            shouldBeInstanceOf<TokenRequest.Invalid>()
            error shouldBe UnauthorizedClient
            description shouldNot beBlank()
        }
    }

    @Test
    fun `should return valid request for valid grant type for given client`(): Unit = runBlocking {

        assertSoftly {
            enumValues<GrantType>().forEach { grantType ->

                every { parameters["grant_type"] } returns grantType.value
                every { client.can(grantType) } returns true

                call.validateTokenRequest(client) shouldBe (tokenRequest)
            }
        }
    }
}