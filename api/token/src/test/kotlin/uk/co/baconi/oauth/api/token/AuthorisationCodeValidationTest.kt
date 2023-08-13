package uk.co.baconi.oauth.api.token

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.ktor.http.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.api.common.authentication.AuthenticatedUsername
import uk.co.baconi.oauth.api.common.authorisation.AuthorisationCode
import uk.co.baconi.oauth.api.common.authorisation.AuthorisationCodeRepository
import uk.co.baconi.oauth.api.common.authorisation.CodeChallenge
import uk.co.baconi.oauth.api.common.authorisation.CodeChallengeMethod
import uk.co.baconi.oauth.api.common.client.ClientAction
import uk.co.baconi.oauth.api.common.client.ClientAction.ProofKeyForCodeExchange
import uk.co.baconi.oauth.api.common.client.ClientId
import uk.co.baconi.oauth.api.common.client.ClientPrincipal
import uk.co.baconi.oauth.api.token.TokenErrorType.InvalidGrant
import uk.co.baconi.oauth.api.token.TokenErrorType.InvalidRequest
import java.time.Instant.now
import java.time.temporal.ChronoUnit.DAYS
import java.util.*

class AuthorisationCodeValidationTest : AuthorisationCodeValidation {

    private val uuid = UUID.randomUUID()

    private val client = mockk<ClientPrincipal> {
        every { id } returns ClientId("consumer-x")
        every { can(any<ClientAction>()) } returns false
        every { hasRedirectUri(any()) } returns true
    }

    private val parameters = mockk<Parameters> {
        every { this@mockk[any()] } returns null
        every { this@mockk["redirect_uri"] } returns "uk.co.baconi.valid://callback"
        every { this@mockk["code"] } returns "$uuid"
    }

    override val authorisationCodeRepository = mockk<AuthorisationCodeRepository>()

    @Nested
    inner class ValidateAuthorisationCodeRequest {

        @Test
        fun `should return invalid request on missing redirect uri`() {

            every { parameters["redirect_uri"] } returns null

            assertSoftly(validateAuthorisationCodeRequest(parameters, client)) {
                shouldBeInstanceOf<TokenRequest.Invalid>()
                error shouldBe InvalidRequest
                description shouldBe "missing parameter: redirect_uri"
            }
        }

        @Test
        fun `should return invalid request on blank redirect uri`() {

            every { parameters["redirect_uri"] } returns "     "

            assertSoftly(validateAuthorisationCodeRequest(parameters, client)) {
                shouldBeInstanceOf<TokenRequest.Invalid>()
                error shouldBe InvalidRequest
                description shouldBe "invalid parameter: redirect_uri"
            }
        }

        @Test
        fun `should return invalid request on invalid redirect uri`() {

            every { parameters["redirect_uri"] } returns "uk.co.baconi.invalid://callback"
            every { client.hasRedirectUri(any()) } returns false

            assertSoftly(validateAuthorisationCodeRequest(parameters, client)) {
                shouldBeInstanceOf<TokenRequest.Invalid>()
                error shouldBe InvalidRequest
                description shouldBe "invalid parameter: redirect_uri"
            }

            verify { client.hasRedirectUri("uk.co.baconi.invalid://callback") }
        }

        @Test
        fun `should return invalid request on missing code`() {

            every { parameters["code"] } returns null

            assertSoftly(validateAuthorisationCodeRequest(parameters, client)) {
                shouldBeInstanceOf<TokenRequest.Invalid>()
                error shouldBe InvalidRequest
                description shouldBe "missing parameter: code"
            }
        }

        @Test
        fun `should return invalid request on invalid code`() {

            every { parameters["code"] } returns "invalid-code"

            assertSoftly(validateAuthorisationCodeRequest(parameters, client)) {
                shouldBeInstanceOf<TokenRequest.Invalid>()
                error shouldBe InvalidGrant
                description shouldBe "invalid parameter: code"
            }
        }

        @Test
        fun `should return invalid request on missing code verifier for PKCE clients`() {

            every { client.can(ProofKeyForCodeExchange) } returns true
            every { parameters["code_verifier"] } returns null

            assertSoftly(validateAuthorisationCodeRequest(parameters, client)) {
                shouldBeInstanceOf<TokenRequest.Invalid>()
                error shouldBe InvalidRequest
                description shouldBe "missing parameter: code_verifier"
            }
        }

        @Test
        fun `should return invalid request on blank code verifier for PKCE clients`() {

            every { client.can(ProofKeyForCodeExchange) } returns true
            every { parameters["code_verifier"] } returns "     "

            assertSoftly(validateAuthorisationCodeRequest(parameters, client)) {
                shouldBeInstanceOf<TokenRequest.Invalid>()
                error shouldBe InvalidRequest
                description shouldBe "invalid parameter: code_verifier"
            }
        }

        @Test
        fun `should return invalid request on invalid authorisation code`() {

            every { parameters["code"] } returns uuid.toString()
            every { authorisationCodeRepository.findById(uuid) } returns null

            assertSoftly(validateAuthorisationCodeRequest(parameters, client)) {
                shouldBeInstanceOf<TokenRequest.Invalid>()
                error shouldBe InvalidGrant
                description shouldBe "invalid parameter: code"
            }
        }

        @Test
        fun `should return valid request on valid authorisation code`() {

            every { authorisationCodeRepository.findById(uuid) } returns AuthorisationCode.Basic(
                value = uuid,
                issuedAt = now(),
                expiresAt = now().plus(1, DAYS),
                clientId = client.id,
                username = AuthenticatedUsername("aardvark"),
                redirectUri = "uk.co.baconi.valid://callback",
                scopes = emptySet(),
                state = UUID.randomUUID().toString()
            )

            assertSoftly(validateAuthorisationCodeRequest(parameters, client)) {
                shouldBeInstanceOf<AuthorisationCodeRequest>()
            }
        }
    }

    @Nested
    inner class ValidateAuthorisationCode {

        @Test
        fun `should return null when authorisation code cannot be found`() {

            every { authorisationCodeRepository.findById(uuid) } returns null

            validateAuthorisationCode(client, uuid, "uk.co.baconi.valid://callback") should beNull()
        }

        @Test
        fun `should return null when client in authorisation code does not match provided`() {

            every { authorisationCodeRepository.findById(uuid) } returns mockk {
                every { clientId } returns ClientId("consumer-y")
            }

            validateAuthorisationCode(client, uuid, "uk.co.baconi.valid://callback") should beNull()
        }

        @Test
        fun `should return null when redirect in authorisation code does not match provided`() {

            every { authorisationCodeRepository.findById(uuid) } returns mockk {
                every { clientId } returns ClientId("consumer-x")
                every { redirectUri } returns "uk.co.baconi.valid://callback"
            }

            validateAuthorisationCode(client, uuid, "uk.co.baconi.invalid://callback") should beNull()
        }

        @Test
        fun `should return null when authorisation code has expired`() {

            every { authorisationCodeRepository.findById(uuid) } returns mockk {
                every { clientId } returns ClientId("consumer-x")
                every { redirectUri } returns "uk.co.baconi.valid://callback"
                every { hasExpired() } returns true
            }

            validateAuthorisationCode(client, uuid, "uk.co.baconi.valid://callback") should beNull()
        }

        @Test
        fun `should return null when code verifier was not provided but authorisation code is PKCE issued`() {

            every { authorisationCodeRepository.findById(uuid) } returns mockk<AuthorisationCode.PKCE> {
                every { clientId } returns ClientId("consumer-x")
                every { redirectUri } returns "uk.co.baconi.valid://callback"
                every { hasExpired() } returns false
            }

            validateAuthorisationCode(client, uuid, "uk.co.baconi.valid://callback", null) should beNull()
        }

        // TODO - Code verifier mismatch for PKCE authentication codes
        @Test
        fun `should return null when code verifier does not match the challenge in the authorisation code`() {

            every { authorisationCodeRepository.findById(uuid) } returns mockk<AuthorisationCode.PKCE> {
                every { clientId } returns ClientId("consumer-x")
                every { redirectUri } returns "uk.co.baconi.valid://callback"
                every { hasExpired() } returns false
                every { codeChallenge } returns CodeChallenge("z5wcuJWEv4xBdqN8LJVKjcVgd9O6Ze5EAR5iq3xjzi0")
                every { codeChallengeMethod } returns CodeChallengeMethod.S256
            }

            validateAuthorisationCode(client, uuid, "uk.co.baconi.valid://callback", "invalid_verifier") should beNull()
        }

        @Test
        fun `should return pkce authorisation code if the code verifier matches`() {

            every { authorisationCodeRepository.findById(uuid) } returns mockk<AuthorisationCode.PKCE> {
                every { value } returns uuid
                every { clientId } returns ClientId("consumer-x")
                every { redirectUri } returns "uk.co.baconi.valid://callback"
                every { hasExpired() } returns false
                every { codeChallenge } returns CodeChallenge("z5wcuJWEv4xBdqN8LJVKjcVgd9O6Ze5EAR5iq3xjzi0")
                every { codeChallengeMethod } returns CodeChallengeMethod.S256
            }

            assertSoftly(validateAuthorisationCode(client, uuid, "uk.co.baconi.valid://callback", "aardvark")) {
                shouldBeInstanceOf<AuthorisationCode>()
                value shouldBe uuid
            }
        }

        @Test
        fun `should return null when code verifier provide but authorisation code is not PKCE issued`() {

            every { authorisationCodeRepository.findById(uuid) } returns mockk<AuthorisationCode.Basic> {
                every { clientId } returns ClientId("consumer-x")
                every { redirectUri } returns "uk.co.baconi.valid://callback"
                every { hasExpired() } returns false
            }

            validateAuthorisationCode(client, uuid, "uk.co.baconi.valid://callback", "code_verifier") should beNull()
        }

        @Test
        fun `should return authorisation code when its valid`() {

            every { authorisationCodeRepository.findById(uuid) } returns mockk<AuthorisationCode.Basic> {
                every { value } returns uuid
                every { clientId } returns ClientId("consumer-x")
                every { redirectUri } returns "uk.co.baconi.valid://callback"
                every { hasExpired() } returns false
            }

            assertSoftly(validateAuthorisationCode(client, uuid, "uk.co.baconi.valid://callback")) {
                shouldBeInstanceOf<AuthorisationCode>()
                value shouldBe uuid
            }
        }
    }

    @Nested
    inner class ValidateCodeVerifier {

        @Test
        fun `should return false if the verifier fails to match the challenge`() {

            val code = AuthorisationCode.PKCE(
                value = UUID.randomUUID(),
                issuedAt = now(),
                expiresAt = now().plus(1, DAYS),
                clientId = ClientId("consumer-x"),
                username = AuthenticatedUsername("username"),
                redirectUri = "uk.co.baconi.valid://callback",
                scopes = emptySet(),
                state = UUID.randomUUID().toString(),
                codeChallenge = CodeChallenge(""),
                codeChallengeMethod = CodeChallengeMethod.S256
            )

            validateCodeVerifier(code, "not-valid") shouldBe false
        }

        @Test
        fun `should return true if the verifier matches the challenge`() {

            val code = AuthorisationCode.PKCE(
                value = UUID.randomUUID(),
                issuedAt = now(),
                expiresAt = now().plus(1, DAYS),
                clientId = ClientId("consumer-x"),
                username = AuthenticatedUsername("username"),
                redirectUri = "uk.co.baconi.valid://callback",
                scopes = emptySet(),
                state = UUID.randomUUID().toString(),
                codeChallenge = CodeChallenge("KABWKWtD0pRRdnAB17BZwxxs83Z3po8TDCO_lruY-L4"),
                codeChallengeMethod = CodeChallengeMethod.S256
            )

            validateCodeVerifier(
                code,
                "hbH.giMPKpzbxY7GbLeEjcom1T9h41bt6Oe8e~Nvs~NwcR6q9JqnysWOoseI2~l4kL5Jl.RTiPU8HzFDz4LaAM5oZQgV6-44OuFZDObal_DJqRFG7QpBuF_yw5FsPleS"
            ) shouldBe true
        }
    }
}